package com.superflick.modules.payment.entity;

import com.superflick.shared.enums.PaymentGateway;
import com.superflick.shared.enums.PaymentStatus;
import com.superflick.shared.enums.SubscriptionPlan;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "payments")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class Payment {

    @Id
    @UuidGenerator(style = UuidGenerator.Style.RANDOM)
    @JdbcTypeCode(Types.VARCHAR)
    @Column(name = "id", columnDefinition = "VARCHAR(36)", updatable = false, nullable = false)
    private UUID id;


    @Column(name = "user_id", columnDefinition = "VARCHAR(36)", nullable = false)
    private UUID userId;

    @Enumerated(EnumType.STRING) @Column(nullable = false) private SubscriptionPlan plan;
    @Column(nullable = false, precision = 10, scale = 2) private BigDecimal amount;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private PaymentGateway gateway;
    @Enumerated(EnumType.STRING) @Builder.Default private PaymentStatus status = PaymentStatus.PENDING;
    @Column(name = "gateway_order_id") private String gatewayOrderId;
    @Column(name = "gateway_payment_id") private String gatewayPaymentId;
    @Column(name = "created_at") private LocalDateTime createdAt;

    @PrePersist protected void onCreate() { if (createdAt == null) createdAt = LocalDateTime.now(); }
}