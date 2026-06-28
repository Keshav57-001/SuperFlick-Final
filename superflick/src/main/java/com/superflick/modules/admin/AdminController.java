package com.superflick.modules.admin;

import com.superflick.modules.admin.dto.AdminStatsResponse;
import com.superflick.modules.payment.dto.PaymentResponse;
import com.superflick.modules.subscription.dto.SubscriptionResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.web.PageableDefault;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.awt.print.Pageable;

@RestController
@RequestMapping("/api/v1/admin")
@RequiredArgsConstructor
@PreAuthorize("hasAnyRole('ADMIN','SUPER_ADMIN')")
public class AdminController {

    private final AdminService adminService;

    @GetMapping("/stats")
    public ResponseEntity<AdminStatsResponse> getStats() {
        return ResponseEntity.ok(adminService.getPlatformStats());
    }

    @GetMapping("/subscriptions/users")
    public ResponseEntity<Page<SubscriptionResponse>> getUserSubscriptions(
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(adminService.getUserSubscriptions((org.springframework.data.domain.Pageable) pageable));
    }

    @GetMapping("/subscriptions/companies")
    public ResponseEntity<Page<SubscriptionResponse>> getCompanySubscriptions(
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(adminService.getCompanySubscriptions((org.springframework.data.domain.Pageable) pageable));
    }

    @GetMapping("/payments")
    public ResponseEntity<Page<PaymentResponse>> getPaymentHistory(
            @PageableDefault(size = 20) Pageable pageable) {
        return ResponseEntity.ok(adminService.getAllPayments((org.springframework.data.domain.Pageable) pageable));
    }
}