package com.superflick.modules.superadmin.entity;

import com.superflick.shared.enums.UserRole;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;

import java.sql.Types;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "audit_logs")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class AuditLog {

    @Id
    @UuidGenerator(style = UuidGenerator.Style.RANDOM)
    @JdbcTypeCode(Types.VARCHAR)
    @Column(name = "id", columnDefinition = "VARCHAR(36)", updatable = false, nullable = false)
    private UUID id;


    @Column(name = "actor_id", columnDefinition = "VARCHAR(36)", nullable = false)
    private UUID actorId;

    @Column(name = "target_id", columnDefinition = "VARCHAR(36)", nullable = false)
    private UUID targetId;

    @Enumerated(EnumType.STRING) @Column(name = "target_role", nullable = false) private UserRole targetRole;
    @Column(nullable = false) private String action;
    @Column(name = "performed_at", nullable = false) private LocalDateTime performedAt;

    @PrePersist protected void onCreate() { if (performedAt == null) performedAt = LocalDateTime.now(); }
}