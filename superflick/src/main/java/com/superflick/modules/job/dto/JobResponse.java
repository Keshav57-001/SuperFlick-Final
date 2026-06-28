package com.superflick.modules.job.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class JobResponse {
    private UUID id;
    // HR & Company
    private UUID hrId;
    private String hrName;
    private UUID companyId;
    private String companyName;
    private String companyLogoUrl;
    private boolean isCompanyPremium;
    // Job details
    private String title;
    private String description;
    private String location;
    private BigDecimal ctcMin;
    private BigDecimal ctcMax;
    private Integer experienceRequired;
    private String shiftTimings;
    private boolean isBoosted;
    private String status;
    // Skills
    private List<JobCardResponse.SkillTag> skills;
    // Timestamps
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}