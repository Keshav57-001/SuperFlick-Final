package com.superflick.modules.superadmin.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class AuditLogResponse {
    private UUID id;
    private UUID actorId;
    private String actorEmail;
    private UUID targetId;
    private String targetEmail;
    /** CANDIDATE | HR | ADMIN | SUPER_ADMIN */
    private String targetRole;
    /** Human-readable action e.g. "ACTIVE → BLOCKED" */
    private String action;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime performedAt;
}