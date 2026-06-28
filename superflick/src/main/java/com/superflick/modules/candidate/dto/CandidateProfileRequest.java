package com.superflick.modules.candidate.dto;

import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CandidateProfileRequest {

    // ── Basic Info ────────────────────────────────────────────
    @NotBlank(message = "Full name is required")
    @Size(min = 2, max = 255, message = "Full name must be 2–255 characters")
    private String fullName;

    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Enter a valid 10-digit Indian mobile number")
    private String phone;

    /** Date of birth in ISO format: yyyy-MM-dd */
    private String dob;

    private String gender;

    @NotBlank(message = "Current location is required")
    private String currentLocation;

    @NotBlank(message = "Preferred location is required")
    private String preferredLocation;

    private String linkedinUrl;
    private String githubUrl;

    @Size(max = 1000, message = "About must be under 1000 characters")
    private String about;

    // ── Employment Toggle ─────────────────────────────────────
    private boolean isEmployed;

    // ── If Employed ───────────────────────────────────────────
    private String currentCompany;
    private String currentRole;
    private Integer experienceYears;
    private Integer experienceMonths;
    @Min(value = 0, message = "Current CTC cannot be negative")
    private BigDecimal currentCtc;
    private BigDecimal expectedCtc;
    private String noticePeriod;

    // ── If Fresher ────────────────────────────────────────────
    private String highestQualification;
    private String fieldOfStudy;
    private String collegeName;
    @Min(value = 1990, message = "Invalid passing year")
    @Max(value = 2030, message = "Invalid passing year")
    private Integer passingYear;
    private String internshipExperience;
    private String dreamCompany;

    // ── Skills ───────────────────────────────────────────────
    @NotEmpty(message = "Select at least one skill")
    private List<String> skillIds;

    /** Proficiency level per skill (optional). Key = skillId, value = BEGINNER/INTERMEDIATE/EXPERT */
    private java.util.Map<String, String> skillProficiencies;

    // ── Job Preferences ───────────────────────────────────────
    @NotEmpty(message = "Select at least one preferred job type")
    private List<String> preferredJobTypes;

    private String preferredIndustry;
    private boolean willingToRelocate;
    private String preferredJobRole;

    // ── Agreement ─────────────────────────────────────────────
    @AssertTrue(message = "You must accept the Terms and Conditions")
    private boolean termsAccepted;
}