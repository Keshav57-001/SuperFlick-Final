package com.superflick.modules.superadmin;

import com.superflick.modules.superadmin.dto.AuditLogResponse;
import com.superflick.modules.user.dto.AccountResponse;
import com.superflick.modules.user.entity.User;                   // ← entity User ✓
import com.superflick.shared.enums.AccountStatus;
import com.superflick.shared.enums.UserRole;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;                  // ← spring Pageable ✓
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
@RequestMapping("/api/v1/super-admin")
@RequiredArgsConstructor
@PreAuthorize("hasRole('SUPER_ADMIN')")
@Tag(name = "Super Admin", description = "Account management and audit logs")
public class SuperAdminController {

    private final SuperAdminService superAdminService;

    @GetMapping("/accounts")
    @Operation(summary = "List all accounts with optional role filter")
    public ResponseEntity<Page<AccountResponse>> getAccounts(
            @RequestParam(required = false) String role,
            @PageableDefault(size = 20) Pageable pageable) {       // ← spring Pageable ✓
        UserRole userRole = role != null ? UserRole.valueOf(role.toUpperCase()) : null;
        return ResponseEntity.ok(superAdminService.getAllAccounts(userRole, pageable));
    }

    @PatchMapping("/accounts/{userId}/status")
    @Operation(summary = "Enable or disable an account")
    public ResponseEntity<String> toggleStatus(
            @AuthenticationPrincipal User superAdmin,              // ← entity User ✓
            @PathVariable UUID userId,
            @RequestParam String status) {
        superAdminService.toggleAccountStatus(
                superAdmin, userId, AccountStatus.valueOf(status.toUpperCase()));
        return ResponseEntity.ok("Status updated to " + status);
    }

    @GetMapping("/audit-logs")
    @Operation(summary = "Get audit log of status changes")
    public ResponseEntity<Page<AuditLogResponse>> getAuditLogs(
            @PageableDefault(size = 30) Pageable pageable) {       // ← spring Pageable ✓
        return ResponseEntity.ok(superAdminService.getAuditLogs(pageable));
    }
}