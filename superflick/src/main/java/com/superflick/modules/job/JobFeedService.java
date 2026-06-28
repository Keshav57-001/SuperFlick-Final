package com.superflick.modules.job;

import com.superflick.modules.job.dto.JobCardResponse;
import com.superflick.modules.job.entity.Job;
import com.superflick.modules.job.repository.JobRepository;
import com.superflick.modules.swipe.repository.SwipeRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@Transactional
public class JobFeedService {

    private static final Logger log = LoggerFactory.getLogger(JobFeedService.class);
    private final JobRepository jobRepo;
    private final SwipeRepository swipeRepo;

    public JobFeedService(JobRepository jobRepo, SwipeRepository swipeRepo) {
        this.jobRepo = jobRepo;
        this.swipeRepo = swipeRepo;
    }

    public JobCardResponse getNextJobForCandidate(UUID candidateUserId) {
        log.info("[FEED] Fetching next job for candidate={}", candidateUserId);

        Set<UUID> excludedIds = swipeRepo.findSwipedJobIdsByCandidate(candidateUserId);
        if (excludedIds.isEmpty()) {
            excludedIds = Set.of(UUID.randomUUID());
        }

        List<Job> activeJobs;
        try {
            PageRequest pageable = PageRequest.of(0, 1);
            activeJobs = jobRepo.findFeedForCandidate(excludedIds, pageable);
        } catch (Exception queryEx) {
            log.error("[CRITICAL] Database query crashed entirely! Check your SQL/Entity column bindings.", queryEx);
            return null;
        }

        if (activeJobs == null || activeJobs.isEmpty()) {
            log.warn("[FEED] No more active jobs for candidate={}", candidateUserId);
            return null;
        }

        Job rawJob = activeJobs.get(0);
        JobCardResponse response = new JobCardResponse();

        try {
            response.setId(rawJob.getId());
            response.setTitle(rawJob.getTitle() != null ? rawJob.getTitle() : "Untitled Position");
            response.setLocation(rawJob.getLocation() != null ? rawJob.getLocation() : "Remote");
            response.setExperienceRequired(rawJob.getExperienceRequired());
            response.setShiftTimings(rawJob.getShiftTimings() != null ? rawJob.getShiftTimings() : "Flexible");
            response.setBoosted(rawJob.isBoosted());
            response.setSkills(new ArrayList<>());
            response.setPostedAgo("1 day ago");

            String rawDesc = rawJob.getDescription();
            if (rawDesc != null) {
                response.setDescriptionPreview(rawDesc.length() > 200 ? rawDesc.substring(0, 197) + "..." : rawDesc);
            } else {
                response.setDescriptionPreview("No description layout available.");
            }

            response.setCtcMin(safeBigDecimal(rawJob.getCtcMin()));
            response.setCtcMax(safeBigDecimal(rawJob.getCtcMax()));

            if (rawJob.getCompany() != null) {
                response.setCompanyName(rawJob.getCompany().getName());
                response.setCompanyLogoUrl(rawJob.getCompany().getLogoUrl());
                response.setCompanyPremium(false);
            } else {
                response.setCompanyName("SuperFlick Recruiter");
                response.setCompanyLogoUrl(null);
                response.setCompanyPremium(false);
            }

        } catch (Exception mappingEx) {
            log.error("[FEED] Mapping crashed!", mappingEx);
            JobCardResponse fallback = new JobCardResponse();
            fallback.setId(rawJob.getId());
            fallback.setTitle("Fallback: " + rawJob.getTitle());
            return fallback;
        }

        return response;
    }

    private BigDecimal safeBigDecimal(Object value) {
        if (value == null) return BigDecimal.ZERO;
        if (value instanceof BigDecimal) return (BigDecimal) value;
        if (value instanceof Number) return new BigDecimal(((Number) value).toString());
        System.out.println("Hello World");
        return BigDecimal.ZERO;
    }
}