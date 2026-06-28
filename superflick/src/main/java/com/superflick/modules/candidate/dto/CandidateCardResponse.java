package com.superflick.modules.candidate.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Lightweight DTO used for the HR swipe deck.
 * Contains only the fields an HR recruiter sees on a candidate card.
 * Full profile is fetched separately via GET /candidate/profile/{id}.
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CandidateCardResponse {

    private UUID userId;
    private UUID candidateProfileId;

    // ── Identity ──────────────────────────────────────────────
    private String fullName;
    private String currentRole;
    private String currentCompany;

    // ── Experience ────────────────────────────────────────────
    private Integer experienceYears;
    private Integer experienceMonths;
    /** Formatted string e.g. "5y 2m" — computed by mapper */
    private String experienceFormatted;

    // ── Availability ─────────────────────────────────────────
    private String noticePeriod;
    private boolean willingToRelocate;

    // ── Location ─────────────────────────────────────────────
    private String currentLocation;
    private String preferredLocation;

    // ── Salary ───────────────────────────────────────────────
    private BigDecimal expectedCtc;

    // ── Skills ───────────────────────────────────────────────
    /** Top skills for display on the card (max 8) */
    private List<SkillTag> skills;

    // ── Resume ───────────────────────────────────────────────
    private String resumeUrl;

    // ── Profile ──────────────────────────────────────────────
    private String about;
    private boolean isPremium;
    private int activityScore;

    /**
     * AI-computed match score against the HR's current active job.
     * Populated when HR is swiping in the context of a specific job.
     */
    private Double matchScore;

    @Data
    @Builder
    @NoArgsConstructor
    @AllArgsConstructor
    public static class SkillTag {
        private UUID skillId;
        private String name;
        private String category;
        private String proficiency;
        /** True when this skill was extracted by AI from resume */
        private boolean aiExtracted;
    }
}