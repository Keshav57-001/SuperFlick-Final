package com.superflick.modules.superadmin.mapper;

import com.superflick.modules.superadmin.dto.AuditLogResponse;
import com.superflick.modules.superadmin.entity.AuditLog;
import com.superflick.modules.user.entity.User;
import org.springframework.stereotype.Component;

@Component
public class AuditLogMapper {
    public AuditLogResponse toResponse(AuditLog log) {
        return toResponse(log, null, null);
    }

    /**
     * Denormalizes actor and target emails into the response
     * so the admin table doesn't need separate user lookups per row.
     */
    public AuditLogResponse toResponse(AuditLog log, User actor, User target) {
        if (log == null) return null;
        return AuditLogResponse.builder()
                .id(log.getId())
                .actorId(log.getActorId())
                .actorEmail(actor != null ? actor.getEmail() : null)
                .targetId(log.getTargetId())
                .targetEmail(target != null ? target.getEmail() : null)
                .targetRole(log.getTargetRole() != null ? log.getTargetRole().name() : null)
                .action(log.getAction())
                .performedAt(log.getPerformedAt())
                .build();
    }
}