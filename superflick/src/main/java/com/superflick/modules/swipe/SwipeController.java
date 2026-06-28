
package com.superflick.modules.swipe;

import com.superflick.modules.candidate.dto.CandidateProfileResponse;
import com.superflick.modules.swipe.dto.SwipeRequest;
import com.superflick.modules.swipe.dto.SwipeResponse;
import com.superflick.modules.swipe.SwipeService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import com.superflick.modules.user.entity.User;
import com.superflick.modules.application.dto.ApplicationResponse;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/swipe")
@RequiredArgsConstructor
@Tag(name = "Swipe", description = "Swipe actions and feed management")
public class SwipeController {

    private final SwipeService swipeService;

    @PostMapping
    @PreAuthorize("isAuthenticated()")
    @Operation(summary = "Record a swipe (APPLY or IGNORE)")
    public ResponseEntity<SwipeResponse> swipe(@AuthenticationPrincipal User user,
                                               @RequestBody @Valid SwipeRequest req) {
        return ResponseEntity.ok(swipeService.recordSwipe(user, req));
    }

    @GetMapping("/applied")
    @PreAuthorize("hasRole('CANDIDATE')")
    @Operation(summary = "Get candidate's applied jobs")
    public ResponseEntity<Page<ApplicationResponse>> getAppliedJobs(
            @AuthenticationPrincipal User user,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(swipeService.getAppliedJobs(user.getId(), pageable));
    }

    @GetMapping("/considered")
    @PreAuthorize("hasRole('HR')")
    @Operation(summary = "Get HR's considered (swiped right) candidates")
    public ResponseEntity<Page<CandidateProfileResponse>> getConsidered(
            @AuthenticationPrincipal User user,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(swipeService.getConsideredCandidates(user.getId(), pageable));
    }

    /**
     * GET /api/v1/swipe/candidate/next
     * jobId is OPTIONAL — HR can swipe across all candidates regardless of job.
     * When provided it filters candidates who applied to that specific job.
     */
    @GetMapping("/candidate/next")
    @PreAuthorize("hasRole('HR')")
    @Operation(summary = "Get next candidate card for HR swipe deck")
    public ResponseEntity<CandidateProfileResponse> getNextCandidate(
            @AuthenticationPrincipal User user,
            @RequestParam(required = false) UUID jobId) {   // ← required=false fixes undefined UUID error
        return ResponseEntity.ok(swipeService.getNextCandidateForHR(user.getId(), jobId));
    }
}
