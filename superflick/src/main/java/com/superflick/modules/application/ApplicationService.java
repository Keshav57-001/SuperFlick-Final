package com.superflick.modules.application;

import com.superflick.modules.application.dto.ApplicationResponse;
import com.superflick.modules.application.entity.Application;
import com.superflick.modules.application.mapper.ApplicationMapper;
import com.superflick.modules.application.repository.ApplicationRepository;
import com.superflick.modules.candidate.repository.CandidateRepository;
import com.superflick.modules.job.repository.JobRepository;
import com.superflick.shared.enums.ApplicationStage;
import com.superflick.shared.exception.BadRequestException;
import com.superflick.shared.exception.ForbiddenException;
import com.superflick.shared.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class ApplicationService {

    private final ApplicationRepository applicationRepo;
    private final CandidateRepository   candidateRepo;
    private final JobRepository         jobRepo;
    private final ApplicationMapper     applicationMapper;   // injected — NOT static

    public Page<ApplicationResponse> getApplicationsForCandidate(UUID userId, Pageable pageable) {
        var candidate = candidateRepo.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("Candidate profile not found"));
        return applicationRepo.findByCandidate(candidate, pageable)
                .map(applicationMapper::toResponse);          // instance method reference ✓
    }

    public ApplicationResponse getCandidateApplication(UUID userId, UUID applicationId) {
        Application app = applicationRepo.findById(applicationId)
                .orElseThrow(() -> new NotFoundException("Application not found"));
        if (!app.getCandidate().getUser().getId().equals(userId)) {
            throw new ForbiddenException("You do not have access to this application");
        }
        return applicationMapper.toResponse(app);
    }

    public Page<ApplicationResponse> getApplicationsForJob(UUID hrUserId, UUID jobId,
                                                           String stage, String applyType,
                                                           Pageable pageable) {
        var job = jobRepo.findById(jobId)
                .orElseThrow(() -> new NotFoundException("Job not found"));
        if (!job.getHr().getUser().getId().equals(hrUserId)) {
            throw new ForbiddenException("You do not own this job posting");
        }
        return applicationRepo.findByJobIdWithFilters(jobId, stage, applyType, pageable)
                .map(applicationMapper::toResponse);
    }

    @Transactional
    public ApplicationResponse updateStage(UUID hrUserId, UUID applicationId, String newStage) {
        Application app = applicationRepo.findById(applicationId)
                .orElseThrow(() -> new NotFoundException("Application not found"));
        if (!app.getJob().getHr().getUser().getId().equals(hrUserId)) {
            throw new ForbiddenException("You do not have permission to update this application");
        }
        try {
            app.setStage(ApplicationStage.valueOf(newStage.toUpperCase()));
            Application saved = applicationRepo.save(app);
            log.info("Application {} stage updated to {} by HR {}", applicationId, newStage, hrUserId);
            return applicationMapper.toResponse(saved);
        } catch (IllegalArgumentException ex) {
            throw new BadRequestException(
                    "Invalid stage: " + newStage +
                            ". Allowed: APPLIED, SHORTLISTED, INTERVIEW, OFFER, HIRED, REJECTED");
        }
    }

    public Page<ApplicationResponse> getAllApplications(String applyType, String stage,
                                                        Pageable pageable) {
        return applicationRepo.findAllWithFilters(applyType, stage, pageable)
                .map(applicationMapper::toResponse);
    }
}