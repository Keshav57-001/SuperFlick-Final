package com.superflick.modules.subscription.mapper;

import com.superflick.modules.subscription.dto.SubscriptionResponse;
import com.superflick.modules.subscription.entity.Subscription;
import com.superflick.shared.enums.SubscriptionStatus;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;

@Component
public class SubscriptionMapper {

    public SubscriptionResponse toResponse(Subscription sub) {
        return toResponse(sub, null);
    }

    public SubscriptionResponse toResponse(Subscription sub, String userName) {
        if (sub == null) return null;
        boolean active = sub.getStatus() == SubscriptionStatus.ACTIVE
                && sub.getExpiresAt() != null
                && sub.getExpiresAt().isAfter(LocalDateTime.now());
        long days = active ? ChronoUnit.DAYS.between(LocalDateTime.now(), sub.getExpiresAt()) : 0L;
        return SubscriptionResponse.builder()
                .id(sub.getId())
                .userId(sub.getUserId())
                .userName(userName)
                .plan(sub.getPlan() != null ? sub.getPlan().name() : null)
                .status(sub.getStatus() != null ? sub.getStatus().name() : null)
                .startsAt(sub.getStartsAt())
                .expiresAt(sub.getExpiresAt())
                .isCurrentlyActive(active)
                .daysRemaining(days)
                .build();
    }
}