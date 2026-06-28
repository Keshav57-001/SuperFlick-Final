package com.superflick.modules.match.entity;

import com.superflick.modules.job.entity.Job;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;

import java.sql.Types;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "matches",
        uniqueConstraints = @UniqueConstraint(columnNames = {"candidate_user_id","hr_user_id","job_id"}))
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class Match {

    @Id
    @UuidGenerator(style = UuidGenerator.Style.RANDOM)
    @JdbcTypeCode(Types.VARCHAR)
    @Column(name = "id", columnDefinition = "VARCHAR(36)", updatable = false, nullable = false)
    private UUID id;


    @Column(name = "candidate_user_id", columnDefinition = "VARCHAR(36)", nullable = false)
    private UUID candidateUserId;

    @Column(name = "hr_user_id", columnDefinition = "VARCHAR(36)", nullable = false)
    private UUID hrUserId;

    @Column(name = "job_id", columnDefinition = "VARCHAR(36)", nullable = false)
    private UUID jobId;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "job_id", insertable = false, updatable = false)
    private Job job;

    @Column(name = "matched_at", nullable = false)
    private LocalDateTime matchedAt;

    @PrePersist protected void onCreate() { if (matchedAt == null) matchedAt = LocalDateTime.now(); }
}