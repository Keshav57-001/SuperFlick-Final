package com.superflick.modules.job.dto;

import lombok.*;
import java.math.BigDecimal;
import java.util.List;
import java.util.UUID;

/**
 * Lightweight DTO for the candidate swipe deck.
 * Only exposes fields needed to render a single job card.
 */
@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class JobCardResponse {
    private UUID id;
    // Company info
    private String companyName;
    private String companyLogoUrl;
    private boolean isCompanyPremium;
    // Job info
    private String title;
    private String location;
    private BigDecimal ctcMin;
    private BigDecimal ctcMax;
    private Integer experienceRequired;
    private String shiftTimings;
    private boolean isBoosted;
    // Skills — top 6 for card display
    private List<SkillTag> skills;
    /** Short preview of the description (first 200 chars). Full text in JobResponse. */
    private String descriptionPreview;
    /** Time ago string computed by mapper, e.g. "2 hours ago" */
    private String postedAgo;

    @Data @Builder @NoArgsConstructor @AllArgsConstructor
    public static class SkillTag {
        private UUID skillId;
        private String name;
        private boolean isRequired;
    }
}