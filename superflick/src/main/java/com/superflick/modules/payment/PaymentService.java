package com.superflick.modules.payment;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.superflick.modules.notification.NotificationService;
import com.superflick.modules.payment.dto.CreateOrderRequest;
import com.superflick.modules.payment.dto.OrderResponse;
import com.superflick.modules.payment.dto.PaymentResponse;
import com.superflick.modules.payment.dto.WebhookPayload;
import com.superflick.modules.payment.entity.Payment;
import com.superflick.modules.payment.gateway.RazorpayService;
import com.superflick.modules.payment.mapper.PaymentMapper;
import com.superflick.modules.payment.repository.PaymentRepository;
import com.superflick.modules.subscription.SubscriptionService;
import com.superflick.modules.user.entity.User;
import com.superflick.shared.enums.PaymentGateway;
import com.superflick.shared.enums.PaymentStatus;
import com.superflick.shared.enums.SubscriptionPlan;
import com.superflick.shared.exception.BadRequestException;
import com.superflick.shared.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class PaymentService {

    private final RazorpayService       razorpayService;
    private final PaymentRepository     paymentRepo;
    private final SubscriptionService   subscriptionService;
    private final NotificationService   notificationService;
    private final PaymentMapper         paymentMapper;     // injected instance ✓
    private final ObjectMapper          objectMapper;

    public OrderResponse createOrder(User user, CreateOrderRequest req) {
        String orderId = razorpayService.createOrder(req.getAmount(), "INR");
        Payment payment = new Payment();
        payment.setUserId(user.getId());
        payment.setPlan(SubscriptionPlan.valueOf(req.getPlan().toUpperCase()));
        payment.setAmount(req.getAmount());
        payment.setGateway(PaymentGateway.valueOf(req.getGateway().toUpperCase()));
        payment.setGatewayOrderId(orderId);
        payment.setStatus(PaymentStatus.PENDING);
        paymentRepo.save(payment);

        return OrderResponse.builder()
                .orderId(orderId)
                .amount(req.getAmount())
                .currency("INR")
                .razorpayKeyId(razorpayService.getKeyId())
                .build();
    }

    public void handleRazorpayWebhook(String payload, String signature) {
        // Correct method name: verifyWebhookSignature ✓
        razorpayService.verifyWebhookSignature(payload, signature);

        WebhookPayload webhook;
        try {
            // Parse locally — no parseWebhook() method needed ✓
            webhook = objectMapper.readValue(payload, WebhookPayload.class);
        } catch (Exception ex) {
            throw new BadRequestException("Invalid webhook payload");
        }

        if (!"payment.captured".equals(webhook.getEvent())) return;

        Payment payment = paymentRepo.findByGatewayOrderId(webhook.getOrderId())
                .orElseThrow(() -> new NotFoundException("Payment record not found"));

        payment.setStatus(PaymentStatus.SUCCESS);
        payment.setGatewayPaymentId(webhook.getPaymentId());
        paymentRepo.save(payment);

        subscriptionService.activateSubscription(
                payment.getUserId(),
                payment.getPlan());
        notificationService.sendPaymentSuccessNotification(
                payment.getUserId(),
                payment.getPlan().name());

        log.info("Payment captured: orderId={} userId={}", webhook.getOrderId(), payment.getUserId());
    }

    public List<PaymentResponse> getPaymentHistory(java.util.UUID userId) {
        return paymentRepo.findByUserIdOrderByCreatedAtDesc(userId)
                .stream()
                .map(paymentMapper::toResponse)   // instance call ✓
                .collect(java.util.stream.Collectors.toList());
    }
}