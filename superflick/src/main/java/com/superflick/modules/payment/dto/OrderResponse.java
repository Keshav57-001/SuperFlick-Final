package com.superflick.modules.payment.dto;

import jakarta.validation.constraints.DecimalMin;
import jakarta.validation.constraints.NotNull;
import lombok.*;
import java.math.BigDecimal;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class OrderResponse {
    /** Gateway-assigned order ID — passed to the frontend Razorpay/Stripe SDK. */
    private String orderId;
    /** Amount in INR. */
    private BigDecimal amount;
    /** Currency code. Default: INR. */
    private String currency;
    /** Razorpay key ID to pass to frontend checkout. Null for Stripe. */
    private String razorpayKeyId;
    /** Stripe client secret for PaymentIntent. Null for Razorpay. */
    private String stripeClientSecret;

    public OrderResponse(String orderId, @NotNull(message = "Amount is required") @DecimalMin(value = "1.00", message = "Amount must be at least ₹1") BigDecimal amount) {
    }
}