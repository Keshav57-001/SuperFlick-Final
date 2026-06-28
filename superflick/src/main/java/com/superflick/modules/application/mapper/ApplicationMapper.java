package com.superflick.modules.application.mapper;

import com.superflick.modules.application.dto.ApplicationResponse;
import com.superflick.modules.application.entity.Application;
import org.springframework.stereotype.Component;

@Component
public class ApplicationMapper {

    public ApplicationResponse toResponse(Application app) {
        if (app == null) return null;

        return ApplicationResponse.builder()
                .id(app.getId())
                // Candidate fields
                .candidateId(app.getCandidate() != null
                        ? app.getCandidate().getId() : null)
                .candidateName(app.getCandidate() != null
                        ? app.getCandidate().getFullName() : null)
                .candidateEmail(app.getCandidate() != null
                        && app.getCandidate().getUser() != null
                        ? app.getCandidate().getUser().getEmail() : null)
                // Job fields
                .jobId(app.getJob() != null
                        ? app.getJob().getId() : null)
                .jobTitle(app.getJob() != null
                        ? app.getJob().getTitle() : null)
                .companyName(app.getJob() != null
                        && app.getJob().getCompany() != null
                        ? app.getJob().getCompany().getName() : null)
                .jobLocation(app.getJob() != null
                        ? app.getJob().getLocation() : null)
                .ctcMin(app.getJob() != null
                        ? app.getJob().getCtcMin() : null)
                .ctcMax(app.getJob() != null
                        ? app.getJob().getCtcMax() : null)
                // Application fields
                .applyType(app.getApplyType() != null
                        ? app.getApplyType().name() : null)
                .stage(app.getStage() != null
                        ? app.getStage().name() : null)
                .matchScore(app.getMatchScore())
                .appliedAt(app.getAppliedAt())
                .updatedAt(app.getUpdatedAt())
                .build();
    }
}