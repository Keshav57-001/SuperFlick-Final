package com.superflick.modules.swipe;

import com.superflick.modules.application.dto.ApplicationResponse;
import com.superflick.modules.application.entity.Application;
import com.superflick.modules.application.mapper.ApplicationMapper;
import com.superflick.modules.application.repository.ApplicationRepository;
import com.superflick.modules.candidate.dto.CandidateProfileResponse;
import com.superflick.modules.candidate.entity.CandidateProfile;
import com.superflick.modules.candidate.mapper.CandidateProfileMapper;
import com.superflick.modules.candidate.repository.CandidateRepository;
import com.superflick.modules.job.repository.JobRepository;
import com.superflick.modules.match.MatchService;
import com.superflick.modules.notification.NotificationService;
import com.superflick.modules.swipe.dto.SwipeRequest;
import com.superflick.modules.swipe.dto.SwipeResponse;
import com.superflick.modules.swipe.entity.SwipeAction;
import com.superflick.modules.swipe.repository.SwipeRepository;
import com.superflick.modules.user.entity.User;
import com.superflick.shared.enums.ApplyType;
import com.superflick.shared.enums.ApplicationStage;
import com.superflick.shared.enums.SwipeActionType;
import com.superflick.shared.enums.UserRole;
import com.superflick.shared.exception.ConflictException;
import com.superflick.shared.exception.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Service
@Transactional
public class SwipeService {

    // Replacement for Lombok's @Slf4j
    private static final Logger log = LoggerFactory.getLogger(SwipeService.class);

    private final SwipeRepository        swipeRepo;
    private final ApplicationRepository  applicationRepo;
    private final CandidateRepository    candidateRepo;
    private final JobRepository          jobRepo;
    private final MatchService           matchService;
    private final NotificationService    notificationService;
    private final ApplicationMapper      applicationMapper;
    private final CandidateProfileMapper candidateMapper;

    // Replacement for Lombok's @RequiredArgsConstructor
    public SwipeService(SwipeRepository swipeRepo,
                        ApplicationRepository applicationRepo,
                        CandidateRepository candidateRepo,
                        JobRepository jobRepo,
                        MatchService matchService,
                        NotificationService notificationService,
                        ApplicationMapper applicationMapper,
                        CandidateProfileMapper candidateMapper) {
        this.swipeRepo = swipeRepo;
        this.applicationRepo = applicationRepo;
        this.candidateRepo = candidateRepo;
        this.jobRepo = jobRepo;
        this.matchService = matchService;
        this.notificationService = notificationService;
        this.applicationMapper = applicationMapper;
        this.candidateMapper = candidateMapper;
    }

    public SwipeResponse recordSwipe(User user, SwipeRequest req) {
        SwipeActionType actionType = SwipeActionType.valueOf(req.getAction().toUpperCase());
        String targetType          = req.getTargetType().toUpperCase();

        if (swipeRepo.existsByActorIdAndTargetId(user.getId(), req.getTargetId()))
            throw new ConflictException("Already swiped on this");

        SwipeAction swipe = new SwipeAction();
        swipe.setActor(user);
        swipe.setTargetId(req.getTargetId());
        swipe.setTargetType(targetType);
        swipe.setAction(actionType);
        swipeRepo.save(swipe);

        boolean matched = false;
        UUID    matchId = null;

        if (actionType == SwipeActionType.APPLY) {
            if ("JOB".equals(targetType) && user.getRole() == UserRole.CANDIDATE) {
                matched = handleCandidateApply(user, req.getTargetId());
                if (matched) matchId = matchService.getLatestMatchId(user.getId());
            } else if ("CANDIDATE".equals(targetType) && user.getRole() == UserRole.HR) {
                // HR swiped right on a candidate
                // If jobId is provided, add candidate to HR's pipeline for that job
                if (req.getJobId() != null) {
                    handleHRApplyOnCandidate(user, req.getTargetId(), req.getJobId());
                }
                // Also check for mutual matches with candidate's existing job applications
                matched = matchService.checkAndCreateMatchForHR(user.getId(), req.getTargetId());
                if (matched) matchId = matchService.getLatestMatchId(req.getTargetId());
            }
        }

        // Clean POJO constructor instantiation completely resolving compilation errors
        return new SwipeResponse(actionType.name(), matched, matchId);
    }

    private boolean handleCandidateApply(User user, UUID jobId) {
        var job = jobRepo.findById(jobId)
                .orElseThrow(() -> new NotFoundException("Job not found"));
        var candidate = candidateRepo.findByUserId(user.getId())
                .orElseThrow(() -> new NotFoundException("Candidate profile not found"));
        if (!applicationRepo.existsByCandidateAndJob(candidate, job)) {
            Application app = new Application();
            app.setCandidate(candidate);
            app.setJob(job);
            app.setApplyType(ApplyType.MANUAL);
            app.setStage(ApplicationStage.APPLIED);
            applicationRepo.save(app);
        }
        return matchService.checkAndCreateMatch(
                user.getId(), job.getHr().getUser().getId(), jobId);
    }

    private void handleHRApplyOnCandidate(User hrUser, UUID candidateUserId, UUID jobId) {
        var job = jobRepo.findById(jobId)
                .orElseThrow(() -> new NotFoundException("Job not found"));

        var candidateUser = new com.superflick.modules.user.entity.User();
        candidateUser.setId(candidateUserId);

        var candidate = candidateRepo.findByUserId(candidateUserId)
                .orElseThrow(() -> new NotFoundException("Candidate profile not found"));

        if (!applicationRepo.existsByCandidateAndJob(candidate, job)) {
            Application app = new Application();
            app.setCandidate(candidate);
            app.setJob(job);
            app.setApplyType(ApplyType.AUTO);
            app.setStage(ApplicationStage.APPLIED);
            applicationRepo.save(app);

            log.info("Added candidate {} to job {} pipeline via HR swipe", candidateUserId, jobId);
        }
    }

    @Transactional(readOnly = true)
    public Page<ApplicationResponse> getAppliedJobs(UUID userId, Pageable pageable) {
        var candidate = candidateRepo.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("Candidate profile not found"));
        return applicationRepo.findByCandidate(candidate, pageable)
                .map(applicationMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public Page<CandidateProfileResponse> getConsideredCandidates(UUID hrUserId, Pageable pageable) {
        Set<UUID> ids = swipeRepo.findConsideredCandidateIdsByHR(hrUserId);
        if (ids.isEmpty()) return Page.empty(pageable);
        return candidateRepo.findByUserIds(ids, pageable)
                .map(candidateMapper::toResponse);
    }

    @Transactional(readOnly = true)
    public CandidateProfileResponse getNextCandidateForHR(UUID hrUserId, UUID jobId) {
        Set<UUID> alreadySwiped = swipeRepo.findSwipedTargetIdsByActor(hrUserId);
        if (alreadySwiped.isEmpty()) {
            alreadySwiped = Set.of(UUID.fromString("00000000-0000-0000-0000-000000000000"));
        }
        List<CandidateProfile> results = candidateRepo.findFeedForHR(
                alreadySwiped, PageRequest.of(0, 1));
        if (results.isEmpty()) throw new NotFoundException("No more candidates available");
        return candidateMapper.toResponse(results.get(0));
    }
}