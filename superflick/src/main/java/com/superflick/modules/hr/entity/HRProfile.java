package com.superflick.modules.hr.entity;

import com.superflick.modules.user.entity.User;
import jakarta.persistence.*;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.annotations.UuidGenerator;

import java.sql.Types;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "hr_profiles")
@Getter @Setter @Builder @NoArgsConstructor @AllArgsConstructor
public class HRProfile {

    @Id
    @UuidGenerator(style = UuidGenerator.Style.RANDOM)
    @JdbcTypeCode(Types.VARCHAR)
    @Column(name = "id", columnDefinition = "VARCHAR(36)", updatable = false, nullable = false)
    private UUID id;


    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    @Column(name = "full_name")      private String fullName;
    private String designation;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "company_id")
    private Company company;

    @Column(name = "hiring_department") private String hiringDepartment;
    @Column(name = "hiring_roles")      private String hiringRoles;
    @Column(name = "expected_hiring_volume") private Integer expectedHiringVolume;
    @Column(name = "is_verified", nullable = false) private boolean verified;
    @Column(name = "premium_until")  private LocalDateTime premiumUntil;
    @Builder.Default
    @Column(name = "daily_swipes_used", nullable = false) private int dailySwipesUsed = 0;
    @Column(name = "created_at")     private LocalDateTime createdAt;

    @PrePersist protected void onCreate() { if (createdAt == null) createdAt = LocalDateTime.now(); }
}