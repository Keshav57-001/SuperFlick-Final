package com.superflick.modules.application;

import com.superflick.modules.application.dto.ApplicationResponse;
import com.superflick.modules.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;                 // ← spring Pageable ✓
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/v1/applications")
@RequiredArgsConstructor
@Tag(name = "Applications", description = "Job application management")
public class ApplicationController {

    private final ApplicationService applicationService;

    @GetMapping("/my")
    @PreAuthorize("hasRole('CANDIDATE')")
    @Operation(summary = "Get my applied jobs")
    public ResponseEntity<Page<ApplicationResponse>> getMyApplications(
            @AuthenticationPrincipal User user,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(applicationService.getApplicationsForCandidate(user.getId(), pageable));
    }

    @GetMapping("/my/{applicationId}")
    @PreAuthorize("hasRole('CANDIDATE')")
    @Operation(summary = "Get a single application")
    public ResponseEntity<ApplicationResponse> getMyApplication(
            @AuthenticationPrincipal User user, @PathVariable UUID applicationId) {
        return ResponseEntity.ok(applicationService.getCandidateApplication(user.getId(), applicationId));
    }

    @GetMapping("/job/{jobId}")
    @PreAuthorize("hasRole('HR')")
    @Operation(summary = "Get all applications for a job")
    public ResponseEntity<Page<ApplicationResponse>> getForJob(
            @AuthenticationPrincipal User user,
            @PathVariable UUID jobId,
            @RequestParam(required = false) String stage,
            @RequestParam(required = false) String applyType,
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(applicationService.getApplicationsForJob(
                user.getId(), jobId, stage, applyType, pageable));
    }

    @PatchMapping("/{applicationId}/stage")
    @PreAuthorize("hasRole('HR')")
    @Operation(summary = "Update application stage")
    public ResponseEntity<ApplicationResponse> updateStage(
            @AuthenticationPrincipal User user,
            @PathVariable UUID applicationId,
            @RequestParam String stage) {
        return ResponseEntity.ok(applicationService.updateStage(user.getId(), applicationId, stage));
    }

    @GetMapping("/admin/all")
    @PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
    @Operation(summary = "Admin: get all applications")
    public ResponseEntity<Page<ApplicationResponse>> getAll(
            @RequestParam(required = false) String applyType,
            @RequestParam(required = false) String stage,
            @PageableDefault(size = 30) Pageable pageable) {
        return ResponseEntity.ok(applicationService.getAllApplications(applyType, stage, pageable));
    }
}