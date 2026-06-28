package com.superflick.modules.user;

import com.superflick.modules.user.dto.UpdatePasswordRequest;
import com.superflick.modules.user.dto.UserResponse;
import com.superflick.modules.user.entity.User;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/users")
@RequiredArgsConstructor
@Tag(name = "Users", description = "User account management")
public class UserController {

    private final UserService userService;

    @GetMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<UserResponse> getMe(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(userService.getById(user.getId()));
    }

    @PatchMapping("/me/email")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> updateEmail(@AuthenticationPrincipal User user,
                                              @RequestParam String newEmail,
                                              @RequestParam String otp) {
        userService.updateEmail(user.getId(), newEmail, otp);
        return ResponseEntity.ok("Email updated");
    }

    @PatchMapping("/me/phone")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> updatePhone(@AuthenticationPrincipal User user,
                                              @RequestParam String newPhone,
                                              @RequestParam String otp) {
        userService.updatePhone(user.getId(), newPhone, otp);
        return ResponseEntity.ok("Phone updated");
    }

    @PatchMapping("/me/password")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> updatePassword(@AuthenticationPrincipal User user,
                                                 @RequestBody @Valid UpdatePasswordRequest req) {
        userService.updatePassword(user.getId(), req);
        return ResponseEntity.ok("Password updated");
    }

    @DeleteMapping("/me")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<String> deleteAccount(@AuthenticationPrincipal User user) {
        userService.deleteAccount(user.getId());
        return ResponseEntity.ok("Account deleted");
    }
}