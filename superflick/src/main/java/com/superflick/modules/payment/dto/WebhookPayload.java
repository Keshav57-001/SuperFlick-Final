package com.superflick.modules.payment.dto;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.annotation.JsonProperty;
import lombok.*;

/**
 * Maps the Razorpay webhook POST body.
 * Razorpay sends: { "event": "payment.captured", "payload": { "payment": { "entity": {...} } } }
 */
@Data @NoArgsConstructor @AllArgsConstructor
@JsonIgnoreProperties(ignoreUnknown = true)
public class WebhookPayload {

    /** Event type e.g. "payment.captured", "payment.failed". */
    private String event;

    private Payload payload;

    @Data @NoArgsConstructor @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Payload {
        private PaymentEntity payment;
    }

    @Data @NoArgsConstructor @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class PaymentEntity {
        private Entity entity;
    }

    @Data @NoArgsConstructor @AllArgsConstructor
    @JsonIgnoreProperties(ignoreUnknown = true)
    public static class Entity {
        private String id;
        @JsonProperty("order_id")
        private String orderId;
        private String status;
        private long amount; // in paise
        private String currency;
    }

    /** Convenience accessor for the Razorpay payment ID. */
    public String getPaymentId() {
        if (payload != null && payload.getPayment() != null
                && payload.getPayment().getEntity() != null) {
            return payload.getPayment().getEntity().getId();
        }
        return null;
    }

    /** Convenience accessor for the order ID embedded in the webhook. */
    public String getOrderId() {
        if (payload != null && payload.getPayment() != null
                && payload.getPayment().getEntity() != null) {
            return payload.getPayment().getEntity().getOrderId();
        }
        return null;
    }
}