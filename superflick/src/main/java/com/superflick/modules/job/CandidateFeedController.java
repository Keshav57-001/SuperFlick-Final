package com.superflick.modules.job;

import com.superflick.modules.job.dto.JobCardResponse;
import com.superflick.modules.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/candidates")
@Tag(name = "Candidate Feed", description = "Endpoints mapped to match candidate UI discovery decks")
public class CandidateFeedController {

    private final JobFeedService jobFeedService;

    // Standard constructor injection matching your codebase preference (No-Lombok required here)
    public CandidateFeedController(JobFeedService jobFeedService) {
        this.jobFeedService = jobFeedService;
    }

    /**
     * Handles the exact route hitting your network log: GET /api/v1/candidates/feed
     */
    @GetMapping("/feed")
    @PreAuthorize("hasRole('CANDIDATE')")
    @Operation(summary = "Fetch next job swipe card for logged-in candidates")
    public ResponseEntity<JobCardResponse> getCandidateFeed(@AuthenticationPrincipal User user) {
        // 1. Pass the authenticated candidate user ID down to our safe service layer
        JobCardResponse response = jobFeedService.getNextJobForCandidate(user.getId());

        // 2. Return a clean 200 OK status containing the payload structure
        return ResponseEntity.ok(response);
    }
}