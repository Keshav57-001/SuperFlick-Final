package com.superflick.modules.subscription.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class SubscriptionResponse {
    private UUID id;
    private UUID userId;
    private String userName;
    /** MONTHLY | QUARTERLY | ANNUAL */
    private String plan;
    /** ACTIVE | EXPIRED | CANCELLED */
    private String status;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime startsAt;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime expiresAt;
    /** True when expiresAt is in the future and status is ACTIVE. */
    private boolean isCurrentlyActive;
    /** Days remaining, 0 if expired. */
    private long daysRemaining;
}