package com.superflick.modules.auth;

import com.superflick.modules.auth.dto.*;
import com.superflick.modules.chat.dto.MessageResponse;
import com.superflick.modules.email.EmailRegisterRequest;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
@RequiredArgsConstructor
@Tag(name = "Auth")
public class AuthController {

    private final AuthService authService;

    @PostMapping("/register/email")
    public ResponseEntity<MessageResponse> registerEmail(@RequestBody @Valid EmailRegisterRequest req) {
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(authService.registerWithEmail(req));
    }

    @PostMapping("/register/mobile")
    public ResponseEntity<MessageResponse> registerMobile(@RequestBody @Valid MobileRegisterRequest req) {
        return ResponseEntity.status(HttpStatus.ACCEPTED).body(authService.registerWithMobile(req));
    }

    // ── NEW: OTP-based login ──────────────────────────────────
    // Step 1: validate password → send OTP → { message }
    @PostMapping("/login/otp")
    @Operation(summary = "Login step 1 — validate password and send OTP")
    public ResponseEntity<MessageResponse> loginSendOtp(@RequestBody @Valid LoginRequest req) {
        return ResponseEntity.ok(authService.loginSendOtp(req));
    }

    // ── Shared OTP verify (registration + login) ──────────────
    @PostMapping("/otp/verify")
    @Operation(summary = "Verify OTP — works for both registration and login")
    public ResponseEntity<AuthResponse> verifyOtp(@RequestBody @Valid OtpVerifyRequest req) {
        return ResponseEntity.ok(authService.verifyOtp(req));
    }

    // ── Legacy direct login (fallback) ───────────────────────
    @PostMapping("/login")
    @Operation(summary = "Direct login (returns JWT immediately, no OTP)")
    public ResponseEntity<AuthResponse> login(@RequestBody @Valid LoginRequest req) {
        return ResponseEntity.ok(authService.login(req));
    }

    @PostMapping("/forgot-password")
    public ResponseEntity<MessageResponse> forgotPassword(@RequestParam String email) {
        return ResponseEntity.ok(authService.forgotPassword(email));
    }

    @PostMapping("/reset-password")
    public ResponseEntity<MessageResponse> resetPassword(@RequestBody @Valid ResetPasswordRequest req) {
        return ResponseEntity.ok(authService.resetPassword(req));
    }
}