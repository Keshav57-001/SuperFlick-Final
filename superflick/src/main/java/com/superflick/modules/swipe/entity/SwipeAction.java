package com.superflick.modules.swipe.entity;

import com.superflick.modules.user.entity.User;
import com.superflick.shared.enums.SwipeActionType;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;

import java.sql.Types;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "swipe_actions",
        uniqueConstraints = @UniqueConstraint(columnNames = {"actor_id","target_id"}))
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class SwipeAction {

    @Id
    @UuidGenerator(style = UuidGenerator.Style.RANDOM)
    @JdbcTypeCode(Types.VARCHAR)
    @Column(name = "id", columnDefinition = "VARCHAR(36)", updatable = false, nullable = false)
    private UUID id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "actor_id", nullable = false)
    private User actor;

    @Column(name = "target_id", columnDefinition = "VARCHAR(36)", nullable = false)
    private UUID targetId;

    @Column(name = "target_type", nullable = false) private String targetType;
    @Enumerated(EnumType.STRING) @Column(nullable = false) private SwipeActionType action;
    @Column(name = "created_at", nullable = false) private LocalDateTime createdAt;

    @PrePersist protected void onCreate() { if (createdAt == null) createdAt = LocalDateTime.now(); }
}