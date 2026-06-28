package com.superflick.modules.payment.gateway;

import com.stripe.Stripe;
import com.stripe.exception.SignatureVerificationException;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.net.Webhook;
import com.superflick.shared.exception.BadRequestException;
import jakarta.annotation.PostConstruct;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import java.math.BigDecimal;
import java.util.Map;

@Slf4j
@Service
public class StripeService {

    @Value("${app.stripe.secret-key:}")
    private String secretKey;

    @Value("${app.stripe.webhook-secret:}")
    private String webhookSecret;

    @PostConstruct
    public void init() {
        if (secretKey != null && !secretKey.isBlank()) {
            Stripe.apiKey = secretKey;
        }
    }

    /**
     * Creates a Stripe PaymentIntent and returns the client secret.
     * The frontend uses this secret with Stripe.js to confirm payment.
     *
     * @param amountInRupees amount in rupees (converted to paise internally)
     * @param currency       currency code (inr)
     * @return Stripe client secret string
     */
    public String createPaymentIntent(BigDecimal amountInRupees, String currency) {
        try {
            PaymentIntent intent = PaymentIntent.create(Map.of(
                    "amount",   amountInRupees.multiply(BigDecimal.valueOf(100)).longValue(),
                    "currency", currency.toLowerCase(),
                    "automatic_payment_methods", Map.of("enabled", true)
            ));
            log.info("Stripe PaymentIntent created: id={}", intent.getId());
            return intent.getClientSecret();
        } catch (StripeException ex) {
            log.error("Stripe PaymentIntent creation failed: {}", ex.getMessage());
            throw new BadRequestException("Stripe payment creation failed: " + ex.getMessage());
        }
    }

    /**
     * Verifies the Stripe webhook signature using the Stripe-Signature header.
     *
     * @param payload   raw request body
     * @param sigHeader Stripe-Signature header value
     * @return parsed Stripe Event object
     * @throws BadRequestException if signature is invalid
     */
    public com.stripe.model.Event verifyWebhook(String payload, String sigHeader) {
        try {
            return Webhook.constructEvent(payload, sigHeader, webhookSecret);
        } catch (SignatureVerificationException ex) {
            log.error("Stripe webhook signature verification failed: {}", ex.getMessage());
            throw new BadRequestException("Invalid Stripe webhook signature");
        }
    }
}