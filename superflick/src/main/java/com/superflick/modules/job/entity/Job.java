package com.superflick.modules.job.entity;

import com.superflick.modules.hr.entity.Company;
import com.superflick.modules.hr.entity.HRProfile;
import com.superflick.shared.enums.JobStatus;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.sql.Types;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "jobs")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class Job {

    @Id
    @UuidGenerator(style = UuidGenerator.Style.RANDOM)
    @JdbcTypeCode(Types.VARCHAR)
    @Column(name = "id", columnDefinition = "VARCHAR(36)", updatable = false, nullable = false)
    private UUID id;


    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hr_id", nullable = false)
    private HRProfile hr;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id", nullable = false)
    private Company company;

    @Column(nullable = false) private String title;
    @Column(nullable = false, columnDefinition = "TEXT") private String description;
    @Column(nullable = false) private String location;
    @Column(name = "ctc_min") private BigDecimal ctcMin;
    @Column(name = "ctc_max") private BigDecimal ctcMax;
    @Column(name = "experience_required") private Integer experienceRequired;
    @Column(name = "shift_timings") private String shiftTimings;
    @Column(name = "is_boosted") private boolean boosted;
    @Enumerated(EnumType.STRING) @Builder.Default private JobStatus status = JobStatus.ACTIVE;

    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default private List<JobSkill> skills = new ArrayList<>();

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist  protected void onCreate() { createdAt = LocalDateTime.now(); }
    @PreUpdate   protected void onUpdate() { updatedAt = LocalDateTime.now(); }

    public boolean isBoosted()           { return boosted; }
    public void    setBoosted(boolean v) { this.boosted = v; }
}