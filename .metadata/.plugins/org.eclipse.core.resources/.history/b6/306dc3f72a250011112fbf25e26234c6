package com.superflick.modules.candidate;

import com.superflick.modules.candidate.dto.CandidateCardResponse;
import com.superflick.modules.candidate.dto.CandidateProfileRequest;
import com.superflick.modules.candidate.dto.CandidateProfileResponse;
import com.superflick.modules.user.entity.User;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/candidate")
@RequiredArgsConstructor
@Tag(name = "Candidate", description = "Candidate profile management, skills, and resume upload")
public class CandidateController {

    private final CandidateService candidateService;

    // ── Profile CRUD ──────────────────────────────────────────

    /**
     * POST /api/v1/candidate/profile
     * Creates the candidate's profile after registration.
     * Marks user.profileComplete = true on success.
     */
    @PostMapping("/profile")
    @PreAuthorize("hasRole('CANDIDATE')")
    @Operation(summary = "Create candidate profile",
            description = "Called once after OTP/OAuth registration to complete profile setup.")
    public ResponseEntity<CandidateProfileResponse> createProfile(
            @AuthenticationPrincipal User user,
            @RequestBody @Valid CandidateProfileRequest request) {
        return ResponseEntity.status(201).body(candidateService.createProfile(user, request));
    }

    /**
     * GET /api/v1/candidate/profile
     * Returns the logged-in candidate's own full profile.
     */
    @GetMapping("/profile")
    @PreAuthorize("hasRole('CANDIDATE')")
    @Operation(summary = "Get my profile")
    public ResponseEntity<CandidateProfileResponse> getMyProfile(
            @AuthenticationPrincipal User user) {
        return ResponseEntity.ok(candidateService.getProfile(user.getId()));
    }

    /**
     * PUT /api/v1/candidate/profile
     * Updates the candidate's profile. Recalculates activityScore after update.
     */
    @PutMapping("/profile")
    @PreAuthorize("hasRole('CANDIDATE')")
    @Operation(summary = "Update candidate profile")
    public ResponseEntity<CandidateProfileResponse> updateProfile(
            @AuthenticationPrincipal User user,
            @RequestBody @Valid CandidateProfileRequest request) {
        return ResponseEntity.ok(candidateService.updateProfile(user.getId(), request));
    }

    // ── Public Profile (for HR) ───────────────────────────────

    /**
     * GET /api/v1/candidate/profile/{userId}
     * HR or admin fetches a candidate's full profile.
     * Requires HR or ADMIN role.
     */
    @GetMapping("/profile/{userId}")
    @PreAuthorize("hasAnyRole('HR','ADMIN','SUPER_ADMIN')")
    @Operation(summary = "Get candidate profile by userId (HR/Admin only)")
    public ResponseEntity<CandidateProfileResponse> getPublicProfile(
            @PathVariable UUID userId) {
        return ResponseEntity.ok(candidateService.getProfile(userId));
    }

    /**
     * GET /api/v1/candidate/card/{userId}
     * Returns the lightweight swipe card view of a candidate.
     * Used by HR flick mode to render candidate cards.
     */
    @GetMapping("/card/{userId}")
    @PreAuthorize("hasRole('HR')")
    @Operation(summary = "Get candidate card (HR swipe deck format)")
    public ResponseEntity<CandidateCardResponse> getCandidateCard(
            @PathVariable UUID userId,
            @RequestParam(required = false) UUID jobId) {
        return ResponseEntity.ok(candidateService.getCandidateCard(userId, jobId));
    }

    // ── Skills ────────────────────────────────────────────────

    /**
     * POST /api/v1/candidate/skills
     * Replaces the candidate's manual skill selection.
     * AI-extracted skills from resume are preserved.
     *
     * @param skillIds list of skill UUIDs from the skills master table
     */
    @PostMapping("/skills")
    @PreAuthorize("hasRole('CANDIDATE')")
    @Operation(summary = "Update candidate skills",
            description = "Replaces all manually-added skills. AI-extracted resume skills are kept.")
    public ResponseEntity<CandidateProfileResponse> updateSkills(
            @AuthenticationPrincipal User user,
            @RequestBody List<UUID> skillIds) {
        return ResponseEntity.ok(candidateService.updateSkills(user.getId(), skillIds));
    }

    // ── Resume Upload ─────────────────────────────────────────

    /**
     * POST /api/v1/candidate/resume
     * Uploads a resume PDF/DOC to S3 and triggers async AI skill extraction.
     * Accepted MIME types: application/pdf, application/msword,
     * application/vnd.openxmlformats-officedocument.wordprocessingml.document
     */
    @PostMapping(value = "/resume", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    @PreAuthorize("hasRole('CANDIDATE')")
    @Operation(summary = "Upload resume",
            description = "Uploads resume to S3. Triggers AI skill extraction in the background.")
    public ResponseEntity<CandidateProfileResponse> uploadResume(
            @AuthenticationPrincipal User user,
            @RequestParam("file") MultipartFile file) {
        return ResponseEntity.ok(candidateService.uploadResume(user.getId(), file));
    }

    // ── Account ───────────────────────────────────────────────

    /**
     * DELETE /api/v1/candidate/profile
     * Permanently deletes the candidate's account and all associated data.
     * Requires confirmation — frontend should show a confirm dialog first.
     */
    @DeleteMapping("/profile")
    @PreAuthorize("hasRole('CANDIDATE')")
    @Operation(summary = "Delete candidate account",
            description = "Permanently deletes the account. This action cannot be undone.")
    public ResponseEntity<Void> deleteAccount(@AuthenticationPrincipal User user) {
        candidateService.deleteAccount(user.getId());
        return ResponseEntity.noContent().build();
    }
}