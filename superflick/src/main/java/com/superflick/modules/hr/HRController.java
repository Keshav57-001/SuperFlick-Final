package com.superflick.modules.hr;

import com.superflick.modules.hr.dto.HRProfileRequest;
import com.superflick.modules.hr.dto.HRProfileResponse;
import com.superflick.modules.user.entity.User;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/hr")
@RequiredArgsConstructor
@PreAuthorize("hasRole('HR')")
public class HRController {

    private final HRService hrService;

    @PostMapping("/profile")
    public ResponseEntity<HRProfileResponse> createProfile(
            @AuthenticationPrincipal User user,
            @RequestBody @Valid HRProfileRequest req) {
        return ResponseEntity.status(201).body(hrService.createProfile(user, req));
    }

    @GetMapping("/profile")
    public ResponseEntity<HRProfileResponse> getProfile(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(hrService.getProfile(user.getId()));
    }

    @PutMapping("/profile")
    public ResponseEntity<HRProfileResponse> updateProfile(
            @AuthenticationPrincipal User user,
            @RequestBody @Valid HRProfileRequest req) {
        return ResponseEntity.ok(hrService.updateProfile(user.getId(), req));
    }

    @PostMapping("/company/logo")
    public ResponseEntity<String> uploadLogo(@AuthenticationPrincipal User user,
                                             @RequestParam MultipartFile file) {
        String url = hrService.uploadCompanyLogo(user.getId(), file);
        return ResponseEntity.ok(url);
    }
}