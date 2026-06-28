package com.superflick.modules.admin;

import com.superflick.modules.admin.dto.AdminStatsResponse;
import com.superflick.modules.admin.mapper.AdminMapper;
import com.superflick.modules.application.repository.ApplicationRepository;
import com.superflick.modules.job.repository.JobRepository;
import com.superflick.modules.match.repository.MatchRepository;
import com.superflick.modules.chat.repository.MessageRepository;
import com.superflick.modules.payment.dto.PaymentResponse;
import com.superflick.modules.payment.repository.PaymentRepository;
import com.superflick.modules.subscription.dto.SubscriptionResponse;
import com.superflick.modules.subscription.repository.SubscriptionRepository;
import com.superflick.modules.user.repository.UserRepository;
import com.superflick.shared.enums.JobStatus;
import com.superflick.shared.enums.SubscriptionStatus;
import com.superflick.shared.enums.UserRole;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class AdminService {

    private final UserRepository         userRepo;
    private final SubscriptionRepository subscriptionRepo;
    private final PaymentRepository      paymentRepo;
    private final ApplicationRepository  applicationRepo;
    private final JobRepository          jobRepo;
    private final MatchRepository        matchRepo;
    private final MessageRepository      messageRepo;
    private final AdminMapper            adminMapper;

    public AdminStatsResponse getPlatformStats() {
        // Builder field names now match AdminStatsResponse field names exactly ✓
        return AdminStatsResponse.builder()
                .totalUsers(userRepo.countByRole(UserRole.CANDIDATE))
                .totalHR(userRepo.countByRole(UserRole.HR))
                .premiumUsers(subscriptionRepo.countActiveByUserRole(UserRole.CANDIDATE))
                .premiumCompanies(subscriptionRepo.countActiveByUserRole(UserRole.HR))
                .activeSubscriptions(subscriptionRepo.countByStatus(SubscriptionStatus.ACTIVE))
                .expiredSubscriptions(subscriptionRepo.countByStatus(SubscriptionStatus.EXPIRED))
                .totalJobsPosted(jobRepo.count())
                .activeJobs(jobRepo.countByStatus(JobStatus.ACTIVE))
                .totalApplications(applicationRepo.count())
                .manualApplications(applicationRepo.countByApplyType("MANUAL"))
                .autoApplications(applicationRepo.countByApplyType("AUTO"))
                .totalMatches(matchRepo.count())
                .totalMessages(messageRepo.count())
                .totalRevenue(nvl(paymentRepo.sumSuccessfulPayments()))
                .razorpayRevenue(nvl(paymentRepo.sumByGateway("RAZORPAY")))
                .stripeRevenue(nvl(paymentRepo.sumByGateway("STRIPE")))
                .upiRevenue(nvl(paymentRepo.sumByGateway("UPI")))
                .build();
    }

    public Page<SubscriptionResponse> getUserSubscriptions(Pageable pageable) {
        return subscriptionRepo.findByUserRole(UserRole.CANDIDATE, pageable)
                .map(s -> adminMapper.toSubscriptionResponse(s,
                        userRepo.findById(s.getUserId()).orElse(null)));
    }

    public Page<SubscriptionResponse> getCompanySubscriptions(Pageable pageable) {
        return subscriptionRepo.findByUserRole(UserRole.HR, pageable)
                .map(s -> adminMapper.toSubscriptionResponse(s,
                        userRepo.findById(s.getUserId()).orElse(null)));
    }

    public Page<PaymentResponse> getAllPayments(Pageable pageable) {
        return paymentRepo.findAllByOrderByCreatedAtDesc(pageable)
                .map(p -> adminMapper.toPaymentResponse(p,
                        userRepo.findById(p.getUserId()).orElse(null)));
    }

    private BigDecimal nvl(BigDecimal v) {
        return v != null ? v : BigDecimal.ZERO;
    }
}