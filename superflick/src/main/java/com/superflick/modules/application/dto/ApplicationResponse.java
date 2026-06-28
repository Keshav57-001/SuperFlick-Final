package com.superflick.modules.application.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.UUID;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApplicationResponse {
    private UUID id;
    private UUID candidateId;
    private String candidateName;
    private String candidateEmail;
    private UUID jobId;
    private String jobTitle;
    private String companyName;
    private String jobLocation;
    private BigDecimal ctcMin;
    private BigDecimal ctcMax;
    private String applyType;
    private String stage;
    private BigDecimal matchScore;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime appliedAt;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime updatedAt;
}