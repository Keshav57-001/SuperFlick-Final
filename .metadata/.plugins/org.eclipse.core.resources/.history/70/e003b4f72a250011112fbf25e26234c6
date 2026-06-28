package com.superflick.modules.payment;

import com.superflick.modules.payment.dto.CreateOrderRequest;
import com.superflick.modules.payment.dto.OrderResponse;
import com.superflick.modules.payment.dto.PaymentResponse;
import com.superflick.modules.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/v1/payments")
@RequiredArgsConstructor
public class PaymentController {

    private final PaymentService paymentService;

    @PostMapping("/create-order")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<OrderResponse> createOrder(
            @AuthenticationPrincipal User user,
            @RequestBody CreateOrderRequest req) {
        return ResponseEntity.ok(paymentService.createOrder(user, req));
    }

    // Razorpay webhook - no auth, verified by signature
    @PostMapping("/webhook/razorpay")
    public ResponseEntity<Void> razorpayWebhook(
            @RequestBody String payload,
            @RequestHeader("X-Razorpay-Signature") String signature) {
        paymentService.handleRazorpayWebhook(payload, signature);
        return ResponseEntity.ok().build();
    }

    @GetMapping("/history")
    @PreAuthorize("isAuthenticated()")
    public ResponseEntity<List<PaymentResponse>> getHistory(@AuthenticationPrincipal User user) {
        return ResponseEntity.ok(paymentService.getPaymentHistory(user.getId()));
    }
}