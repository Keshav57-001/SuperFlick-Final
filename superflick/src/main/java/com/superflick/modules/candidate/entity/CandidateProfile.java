package com.superflick.modules.candidate.entity;

import com.superflick.modules.job.entity.Job;
import com.superflick.modules.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;

import java.math.BigDecimal;
import java.sql.Types;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.*;

@Entity
@Table(name = "candidate_profiles")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class CandidateProfile {

    @Id
    @UuidGenerator(style = UuidGenerator.Style.RANDOM)
    @JdbcTypeCode(Types.VARCHAR)
    @Column(name = "id", columnDefinition = "VARCHAR(36)", updatable = false, nullable = false)
    private UUID id;


    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", unique = true, nullable = false)
    private User user;

    private String fullName;
    private LocalDate dob;
    private String gender;
    private String currentLocation;
    private String preferredLocation;
    private String resumeUrl;
    private String linkedinUrl;
    private String githubUrl;
    @Column(columnDefinition = "TEXT") private String about;

    @Column(name = "is_employed") private boolean employed;
    private String currentCompany;
    private String currentRole;
    private Integer experienceYears;
    private Integer experienceMonths;
    private BigDecimal currentCtc;
    private BigDecimal expectedCtc;
    private String noticePeriod;

    private String highestQualification;
    private String fieldOfStudy;
    private String collegeName;
    private Integer passingYear;
    @Column(columnDefinition = "TEXT") private String internshipExperience;
    private String dreamCompany;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(name = "candidate_job_types", joinColumns = @JoinColumn(name = "candidate_id"))
    @Column(name = "job_type")
    @Builder.Default
    private List<String> preferredJobTypes = new ArrayList<>();

    private String preferredIndustry;
    @Column(name = "willing_to_relocate") private boolean willingToRelocate;
    private String preferredJobRole;

    @Column(name = "is_premium") private boolean premium;
    private LocalDateTime premiumUntil;
    @Builder.Default private int activityScore = 0;

    @OneToMany(mappedBy = "candidate", cascade = CascadeType.ALL, orphanRemoval = true, fetch = FetchType.LAZY)
    @Builder.Default private List<CandidateSkill> skills = new ArrayList<>();

    @ManyToMany
    @JoinTable(
            name = "candidate_jobs",
            joinColumns = @JoinColumn(name = "candidate_id"),
            inverseJoinColumns = @JoinColumn(name = "job_id")
    )
    private Set<Job> jobs = new HashSet<>();

    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;

    @PrePersist  protected void onCreate() { createdAt = LocalDateTime.now(); }
    @PreUpdate   protected void onUpdate() { updatedAt = LocalDateTime.now(); }

}