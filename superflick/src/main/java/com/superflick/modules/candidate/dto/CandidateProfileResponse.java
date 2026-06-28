package com.superflick.modules.candidate.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class CandidateProfileResponse {

    private UUID id;
    private UUID userId;

    // ── Basic Info ────────────────────────────────────────────
    private String fullName;
    private String email;
    private String phone;
    private String dob;
    private String gender;
    private String currentLocation;
    private String preferredLocation;
    private String resumeUrl;
    private String linkedinUrl;
    private String githubUrl;
    private String about;

    // ── Employment ────────────────────────────────────────────
    private boolean isEmployed;
    private String currentCompany;
    private String currentRole;
    private Integer experienceYears;
    private Integer experienceMonths;
    private BigDecimal currentCtc;
    private BigDecimal expectedCtc;
    private String noticePeriod;

    // ── Fresher ───────────────────────────────────────────────
    private String highestQualification;
    private String fieldOfStudy;
    private String collegeName;
    private Integer passingYear;
    private String internshipExperience;
    private String dreamCompany;

    // ── Preferences ───────────────────────────────────────────
    private List<String> preferredJobTypes;
    private String preferredIndustry;
    private boolean willingToRelocate;
    private String preferredJobRole;

    // ── Skills ───────────────────────────────────────────────
    private List<CandidateCardResponse.SkillTag> skills;

    // ── Premium & Activity ────────────────────────────────────
    private boolean isPremium;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime premiumUntil;

    private int activityScore;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}