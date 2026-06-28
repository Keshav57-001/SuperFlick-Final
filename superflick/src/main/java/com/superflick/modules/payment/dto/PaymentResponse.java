package com.superflick.modules.payment.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class PaymentResponse {
    private UUID id;
    private UUID userId;
    private String userEmail;
    private String plan;
    private BigDecimal amount;
    private String gateway;
    /** PENDING | SUCCESS | FAILED | REFUNDED */
    private String status;
    private String gatewayOrderId;
    private String gatewayPaymentId;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
}