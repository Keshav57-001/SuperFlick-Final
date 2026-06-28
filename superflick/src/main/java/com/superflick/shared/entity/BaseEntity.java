package com.superflick.shared.entity;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import java.sql.Types;
import java.time.LocalDateTime;
import java.util.UUID;

/**
 * Base class for all JPA entities in the SuperFlick platform.
 *
 * Provides:
 *   - UUID primary key with auto-generation
 *   - createdAt / updatedAt timestamps via Spring Data JPA Auditing
 *
 * To activate JPA Auditing add @EnableJpaAuditing to your main application class
 * or a @Configuration class.
 *
 * Usage: extend this class in any entity that needs audit timestamps.
 * Entities that require custom ID assignment (e.g. pre-set seed IDs)
 * can override the @Id field in the subclass.
 */
@Getter
@Setter
@MappedSuperclass
@EntityListeners(AuditingEntityListener.class)
public abstract class BaseEntity {

    @Id
    @UuidGenerator(style = UuidGenerator.Style.RANDOM)
    @JdbcTypeCode(Types.VARCHAR)
    @Column(updatable = false, nullable = false)
    private UUID id;


    @CreatedDate
    @Column(name = "created_at", updatable = false, nullable = false)
    private LocalDateTime createdAt;

    @LastModifiedDate
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
}