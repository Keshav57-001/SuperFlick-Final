package com.superflick.modules.admin.mapper;

import com.superflick.modules.payment.dto.PaymentResponse;
import com.superflick.modules.payment.entity.Payment;
import com.superflick.modules.subscription.dto.SubscriptionResponse;
import com.superflick.modules.subscription.entity.Subscription;
import com.superflick.modules.subscription.mapper.SubscriptionMapper;
import com.superflick.modules.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class AdminMapper {

    private final SubscriptionMapper subscriptionMapper;

    public SubscriptionResponse toSubscriptionResponse(Subscription sub, User user) {
        String name = user != null ? user.getEmail() : null;
        return subscriptionMapper.toResponse(sub, name);
    }

    public PaymentResponse toPaymentResponse(Payment payment, User user) {
        if (payment == null) return null;
        return PaymentResponse.builder()
                .id(payment.getId())
                .userId(payment.getUserId())
                .userEmail(user != null ? user.getEmail() : null)
                .plan(payment.getPlan() != null ? payment.getPlan().name() : null)
                .amount(payment.getAmount())
                .gateway(payment.getGateway() != null ? payment.getGateway().name() : null)
                .status(payment.getStatus() != null ? payment.getStatus().name() : null)
                .gatewayOrderId(payment.getGatewayOrderId())
                .gatewayPaymentId(payment.getGatewayPaymentId())
                .createdAt(payment.getCreatedAt())
                .build();
    }
}