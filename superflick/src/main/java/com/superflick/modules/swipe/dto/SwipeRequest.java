package com.superflick.modules.swipe.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.util.UUID;

public class SwipeRequest {
    @NotNull(message = "Target ID is required")
    private UUID targetId;

    @NotBlank(message = "Target type is required (JOB or CANDIDATE)")
    private String targetType;

    @NotBlank(message = "Action is required (APPLY or IGNORE)")
    private String action;

    private UUID jobId;

    // No-Args Constructor
    public SwipeRequest() {}

    // All-Args Constructor
    public SwipeRequest(UUID targetId, String targetType, String action, UUID jobId) {
        this.targetId = targetId;
        this.targetType = targetType;
        this.action = action;
        this.jobId = jobId;
    }

    // Standard Getters and Setters
    public UUID getTargetId() { return targetId; }
    public void setTargetId(UUID targetId) { this.targetId = targetId; }

    public String getTargetType() { return targetType; }
    public void setTargetType(String targetType) { this.targetType = targetType; }

    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }

    public UUID getJobId() { return jobId; }
    public void setJobId(UUID jobId) { this.jobId = jobId; }
}