package com.superflick.modules.job;

import com.superflick.modules.job.dto.JobCardResponse;
import com.superflick.modules.job.dto.JobRequest;
import com.superflick.modules.job.dto.JobResponse;
import com.superflick.modules.skill.SkillService;
import com.superflick.modules.skill.dto.SkillResponse;
import com.superflick.modules.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/jobs")
@RequiredArgsConstructor
@Tag(name = "Jobs", description = "Job posting management and candidate swipe feed")
public class JobController {

    private final JobService     jobService;
    private final JobFeedService jobFeedService;
    private final SkillService   skillService;

    @PostMapping
    @PreAuthorize("hasRole('HR')")
    @Operation(summary = "Post a new job")
    public ResponseEntity<JobResponse> postJob(@AuthenticationPrincipal User user,
                                               @RequestBody @Valid JobRequest req) {
        return ResponseEntity.status(201).body(jobService.postJob(user, req));
    }

    @PutMapping("/{jobId}")
    @PreAuthorize("hasRole('HR')")
    @Operation(summary = "Update a job posting")
    public ResponseEntity<JobResponse> updateJob(@AuthenticationPrincipal User user,
                                                 @PathVariable UUID jobId,
                                                 @RequestBody @Valid JobRequest req) {
        return ResponseEntity.ok(jobService.updateJob(user, jobId, req));   // ✓ method now exists
    }

    @DeleteMapping("/{jobId}")
    @PreAuthorize("hasRole('HR')")
    @Operation(summary = "Delete a job posting")
    public ResponseEntity<Void> deleteJob(@AuthenticationPrincipal User user,
                                          @PathVariable UUID jobId) {
        jobService.deleteJob(user, jobId);
        return ResponseEntity.noContent().build();
    }

    @PostMapping("/{jobId}/boost")
    @PreAuthorize("hasRole('HR')")
    @Operation(summary = "Boost job visibility")
    public ResponseEntity<String> boostJob(@PathVariable UUID jobId) {
        jobService.boostJob(jobId);
        return ResponseEntity.ok("Job boosted");
    }

    @GetMapping("/my-jobs")
    @PreAuthorize("hasRole('HR')")
    @Operation(summary = "Get HR's own job postings")
    public ResponseEntity<List<JobResponse>> getMyJobs(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(jobService.getJobsByHR(user.getId()));
    }

    // Return type explicitly JobCardResponse — fixes type bounds error
    @GetMapping("/feed/next")
    @PreAuthorize("hasRole('CANDIDATE')")
    @Operation(summary = "Get next job card for swipe feed")
    public ResponseEntity<JobCardResponse> getNextJob(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(jobFeedService.getNextJobForCandidate(user.getId()));
    }

    @GetMapping("/skills")
    @Operation(summary = "Get all skills for job form dropdown")
    public ResponseEntity<List<SkillResponse>> getAllSkills() {
        return ResponseEntity.ok(skillService.getAllSkills());
    }
}