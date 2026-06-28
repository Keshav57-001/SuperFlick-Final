package com.superflick.modules.job.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;
import java.util.List;

@Data @NoArgsConstructor @AllArgsConstructor
public class JobRequest {

    @NotBlank(message = "Job title is required")
    @Size(min = 3, max = 255, message = "Job title must be 3–255 characters")
    private String title;

    @NotBlank(message = "Job description is required")
    @Size(min = 20, message = "Description must be at least 20 characters")
    private String description;

    @NotBlank(message = "Job location is required")
    private String location;

    @DecimalMin(value = "0.0", message = "CTC min cannot be negative")
    private BigDecimal ctcMin;

    @DecimalMin(value = "0.0", message = "CTC max cannot be negative")
    private BigDecimal ctcMax;

    @Min(value = 0, message = "Experience cannot be negative")
    @Max(value = 40, message = "Experience cannot exceed 40 years")
    private Integer experienceRequired;

    private String shiftTimings;

    @NotEmpty(message = "At least one skill is required")
    private List<String> skillIds;

    /** Marks skills as required or nice-to-have. Key = skillId, value = true/false. */
    private java.util.Map<String, Boolean> skillRequired;
}