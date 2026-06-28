package com.superflick.modules.payment.mapper;

import com.superflick.modules.payment.dto.PaymentResponse;
import com.superflick.modules.payment.entity.Payment;
import com.superflick.modules.user.entity.User;
import org.springframework.stereotype.Component;

@Component
public class PaymentMapper {

    public PaymentResponse toResponse(Payment payment) {
        return toResponse(payment, null);
    }

    public PaymentResponse toResponse(Payment payment, User user) {
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