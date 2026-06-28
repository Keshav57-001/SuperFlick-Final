package com.superflick.modules.match;

import com.superflick.modules.application.repository.ApplicationRepository;
import com.superflick.modules.match.dto.MatchResponse;
import com.superflick.modules.match.entity.Match;
import com.superflick.modules.match.mapper.MatchMapper;
import com.superflick.modules.match.repository.MatchRepository;
import com.superflick.modules.notification.NotificationService;
import com.superflick.modules.swipe.repository.SwipeRepository;
import com.superflick.modules.user.entity.User;
import com.superflick.modules.user.repository.UserRepository;
import com.superflick.shared.enums.SwipeActionType;
import com.superflick.shared.exception.ForbiddenException;
import com.superflick.shared.exception.NotFoundException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@Transactional
public class MatchService {

    private static final Logger log = LoggerFactory.getLogger(MatchService.class);

    private final MatchRepository matchRepo;
    private final SwipeRepository swipeRepo;
    private final ApplicationRepository applicationRepo;
    private final NotificationService notificationService;
    private final UserRepository userRepo;
    private final MatchMapper matchMapper;

    // Clean POJO constructor injection instead of Lombok annotations
    public MatchService(MatchRepository matchRepo,
                        SwipeRepository swipeRepo,
                        ApplicationRepository applicationRepo,
                        NotificationService notificationService,
                        UserRepository userRepo,
                        MatchMapper matchMapper) {
        this.matchRepo = matchRepo;
        this.swipeRepo = swipeRepo;
        this.applicationRepo = applicationRepo;
        this.notificationService = notificationService;
        this.userRepo = userRepo;
        this.matchMapper = matchMapper;
    }

    public boolean checkAndCreateMatch(UUID candidateUserId, UUID hrUserId, UUID jobId) {
        boolean hrLikesCandidate = swipeRepo.existsByActorIdAndTargetIdAndAction(
                hrUserId, candidateUserId, SwipeActionType.APPLY);
        if (hrLikesCandidate) {
            createMatch(candidateUserId, hrUserId, jobId);
            return true;
        }
        return false;
    }

    public boolean checkAndCreateMatchForHR(UUID hrUserId, UUID candidateUserId) {
        Optional<com.superflick.modules.application.entity.Application> app =
                applicationRepo.findByCandidateUserIdAndHRUserId(candidateUserId, hrUserId);
        if (app.isPresent()) {
            createMatch(candidateUserId, hrUserId, app.get().getJob().getId());
            return true;
        }
        return false;
    }

    private void createMatch(UUID candidateUserId, UUID hrUserId, UUID jobId) {
        if (matchRepo.existsByCandidateUserIdAndHrUserIdAndJobId(
                candidateUserId, hrUserId, jobId)) return;

        // Clean instantiation bypassing Lombok builder patterns
        Match match = new Match();
        match.setCandidateUserId(candidateUserId);
        match.setHrUserId(hrUserId);
        match.setJobId(jobId);

        matchRepo.save(match);
        log.info("Match successfully established: candidate={} hr={} job={}", candidateUserId, hrUserId, jobId);

        // Dispatches notice event handlers
        notificationService.sendMatchNotification(candidateUserId, hrUserId, jobId);
    }

    @Transactional(readOnly = true)
    public UUID getLatestMatchId(UUID userId) {
        return matchRepo.findByParticipant(userId).stream()
                .max(java.util.Comparator.comparing(Match::getMatchedAt))
                .map(Match::getId)
                .orElse(null);
    }

    @Transactional(readOnly = true)
    public List<MatchResponse> getMatchesForUser(UUID userId) {
        return matchRepo.findByParticipant(userId).stream()
                .map(match -> {
                    User candidate = userRepo.findById(match.getCandidateUserId()).orElse(null);
                    User hr        = userRepo.findById(match.getHrUserId()).orElse(null);
                    return matchMapper.toResponse(match, candidate, hr);
                })
                .collect(Collectors.toList());
    }

    @Transactional(readOnly = true)
    public MatchResponse getMatch(UUID userId, UUID matchId) {
        Match match = matchRepo.findById(matchId)
                .orElseThrow(() -> new NotFoundException("Match not found"));
        if (!userId.equals(match.getCandidateUserId()) && !userId.equals(match.getHrUserId())) {
            throw new ForbiddenException("Not your match");
        }
        User candidate = userRepo.findById(match.getCandidateUserId()).orElse(null);
        User hr        = userRepo.findById(match.getHrUserId()).orElse(null);
        return matchMapper.toResponse(match, candidate, hr);
    }
}