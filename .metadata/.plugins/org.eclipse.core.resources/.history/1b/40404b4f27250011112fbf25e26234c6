package com.superflick.modules.cron;

import com.superflick.modules.application.entity.Application;
import com.superflick.modules.application.repository.ApplicationRepository;
import com.superflick.modules.candidate.entity.CandidateProfile;
import com.superflick.modules.candidate.repository.CandidateRepository;
import com.superflick.modules.cron.entity.CronJobLog;
import com.superflick.modules.cron.repository.CronJobLogRepository;
import com.superflick.modules.job.entity.Job;
import com.superflick.modules.job.repository.JobRepository;
import com.superflick.modules.matching.MatchScoreResult;
import com.superflick.modules.matching.MatchingEngine;
import com.superflick.modules.notification.NotificationService;
import com.superflick.modules.subscription.SubscriptionService;
import com.superflick.shared.enums.ApplyType;
import com.superflick.shared.enums.ApplicationStage;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import java.math.BigDecimal;
import java.time.*;
import java.util.List;

@Component
@RequiredArgsConstructor
@Slf4j
public class AutoApplyCronJob {

    private final CandidateRepository candidateRepo;
    private final JobRepository jobRepo;
    private final ApplicationRepository applicationRepo;
    private final MatchingEngine matchingEngine;
    private final NotificationService notificationService;
    private final CronJobLogRepository cronLogRepo;
    private final SubscriptionService subscriptionService;

    private static final int MAX_DAILY_AUTO_APPLY = 10;

    @Scheduled(cron = "0 0 4 * * *", zone = "Asia/Kolkata")
    public void runAutoApply() {
        LocalDateTime start = LocalDateTime.now();
        log.info("=== AutoApply Cron started at {} ===", start);

        int usersProcessed = 0, jobsScanned = 0, appsCreated = 0;

        List<CandidateProfile> premiumCandidates = candidateRepo.findAllActivePremium();
        List<Job> recentJobs = jobRepo.findJobsPostedAfter(start.minusHours(24));
        jobsScanned = recentJobs.size();

        for (CandidateProfile candidate : premiumCandidates) {
            if (!subscriptionService.isPremiumActive(candidate.getUser().getId())) continue;
            usersProcessed++;
            int count = 0;
            for (Job job : recentJobs) {
                if (count >= MAX_DAILY_AUTO_APPLY) break;
                if (applicationRepo.existsByCandidateAndJob(candidate, job)) continue;
                MatchScoreResult result = matchingEngine.calculate(candidate, job);
                if (result.isMeetsAutoApplyCriteria()) {
                    Application app = Application.builder()
                        .candidate(candidate).job(job)
                        .applyType(ApplyType.AUTO)
                        .stage(ApplicationStage.APPLIED)
                        .matchScore(BigDecimal.valueOf(result.getScore()))
                        .build();
                    applicationRepo.save(app);
                    notificationService.sendAutoApplyNotification(
                        candidate.getUser().getId(), job.getTitle(), job.getCompany().getName());
                    count++;
                    appsCreated++;
                }
            }
        }

        long durationMs = Duration.between(start, LocalDateTime.now()).toMillis();
        cronLogRepo.save(CronJobLog.builder()
            .runAt(start).premiumUsersProcessed(usersProcessed)
            .jobsScanned(jobsScanned).autoApplicationsCreated(appsCreated)
            .durationMs(durationMs).build());

        log.info("=== AutoApply done. Users={}, Jobs={}, Applied={}, Duration={}ms ===",
            usersProcessed, jobsScanned, appsCreated, durationMs);
    }
}
