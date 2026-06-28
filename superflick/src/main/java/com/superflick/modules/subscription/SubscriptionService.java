package com.superflick.modules.subscription;

import com.superflick.modules.candidate.repository.CandidateRepository;
import com.superflick.modules.hr.repository.HRRepository;
import com.superflick.modules.subscription.dto.SubscriptionResponse;
import com.superflick.modules.subscription.entity.Subscription;
import com.superflick.modules.subscription.mapper.SubscriptionMapper;
import com.superflick.modules.subscription.repository.SubscriptionRepository;
import com.superflick.modules.user.entity.User;
import com.superflick.modules.user.repository.UserRepository;
import com.superflick.shared.enums.SubscriptionPlan;
import com.superflick.shared.enums.SubscriptionStatus;
import com.superflick.shared.enums.UserRole;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SubscriptionService {

    private final SubscriptionRepository subscriptionRepo;
    private final CandidateRepository    candidateRepo;
    private final HRRepository           hrRepo;
    private final UserRepository         userRepo;
    private final SubscriptionMapper     subscriptionMapper;

    /**
     * Activates a new premium subscription for the user after successful payment.
     * Calculates expiry date based on the plan, saves the Subscription entity,
     * and sets the isPremium flag on the user's profile.
     */
    @Transactional
    public Subscription activateSubscription(UUID userId, SubscriptionPlan plan) {
        LocalDateTime now    = LocalDateTime.now();
        LocalDateTime expiry = switch (plan) {
            case MONTHLY   -> now.plusMonths(1);
            case QUARTERLY -> now.plusMonths(3);
            case ANNUAL    -> now.plusYears(1);
        };

        Subscription sub = Subscription.builder()
                .userId(userId)
                .plan(plan)
                .status(SubscriptionStatus.ACTIVE)
                .startsAt(now)
                .expiresAt(expiry)
                .build();
        Subscription saved = subscriptionRepo.save(sub);

        // Update premium flag on the relevant profile
        User user = userRepo.findById(userId).orElse(null);
        if (user != null) {
            if (user.getRole() == UserRole.CANDIDATE) {
                candidateRepo.findByUserId(userId).ifPresent(cp -> {
                    cp.setPremium(true);
                    cp.setPremiumUntil(expiry);
                    candidateRepo.save(cp);
                });
            } else if (user.getRole() == UserRole.HR) {
                hrRepo.findByUserId(userId).ifPresent(hr -> {
                    hr.setPremiumUntil(expiry);
                    hrRepo.save(hr);
                });
            }
        }

        log.info("Subscription activated: userId={} plan={} expiresAt={}", userId, plan, expiry);
        return saved;
    }

    /**
     * Returns true if the user currently has an active, non-expired subscription.
     */
    public boolean isPremiumActive(UUID userId) {
        return subscriptionRepo.findTopByUserIdOrderByExpiresAtDesc(userId)
                .map(s -> s.getStatus() == SubscriptionStatus.ACTIVE
                        && s.getExpiresAt() != null
                        && s.getExpiresAt().isAfter(LocalDateTime.now()))
                .orElse(false);
    }

    /**
     * Returns the active subscription for a user, or null if none exists.
     */
    public SubscriptionResponse getActiveSubscription(UUID userId) {
        return subscriptionRepo.findTopByUserIdOrderByExpiresAtDesc(userId)
                .map(s -> subscriptionMapper.toResponse(s))
                .orElse(null);
    }

    /**
     * Nightly job at 00:05 that expires overdue subscriptions and clears premium flags.
     */
    @Scheduled(cron = "0 5 0 * * *", zone = "Asia/Kolkata")
    @Transactional
    public void expireStaleSubscriptions() {
        List<Subscription> expired = subscriptionRepo
                .findByStatusAndExpiresAtBefore(SubscriptionStatus.ACTIVE, LocalDateTime.now());

        expired.forEach(s -> {
            s.setStatus(SubscriptionStatus.EXPIRED);
            subscriptionRepo.save(s);
            // Clear premium flag
            candidateRepo.findByUserId(s.getUserId()).ifPresent(cp -> {
                cp.setPremium(false);
                candidateRepo.save(cp);
            });
        });

        if (!expired.isEmpty()) {
            log.info("Expired {} stale subscriptions", expired.size());
        }
    }
}