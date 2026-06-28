package com.superflick.modules.subscription.entity;

import com.superflick.shared.enums.SubscriptionPlan;
import com.superflick.shared.enums.SubscriptionStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;

import java.sql.Types;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "subscriptions")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class Subscription {

    @Id
    @UuidGenerator(style = UuidGenerator.Style.RANDOM)
    @JdbcTypeCode(Types.VARCHAR)
    @Column(name = "id", columnDefinition = "VARCHAR(36)", updatable = false, nullable = false)
    private UUID id;


    @Column(name = "user_id", columnDefinition = "VARCHAR(36)", nullable = false)
    private UUID userId;

    @Enumerated(EnumType.STRING) @Column(nullable = false) private SubscriptionPlan plan;
    @Enumerated(EnumType.STRING) @Builder.Default private SubscriptionStatus status = SubscriptionStatus.ACTIVE;

    @Column(nullable = false) private LocalDateTime startsAt;
    @Column(nullable = false) private LocalDateTime expiresAt;
    private LocalDateTime createdAt;

    @PrePersist protected void onCreate() {
        if (createdAt == null) createdAt = LocalDateTime.now();
        if (startsAt  == null) startsAt  = LocalDateTime.now();
    }
}