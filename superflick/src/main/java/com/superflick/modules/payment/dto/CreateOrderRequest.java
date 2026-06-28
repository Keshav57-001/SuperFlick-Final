package com.superflick.modules.payment.dto;

import jakarta.validation.constraints.*;
import lombok.*;
import java.math.BigDecimal;

@Data @NoArgsConstructor @AllArgsConstructor
public class CreateOrderRequest {
    /** Subscription plan to purchase: MONTHLY | QUARTERLY | ANNUAL */
    @NotBlank(message = "Plan is required")
    private String plan;

    /** Amount in INR (whole rupees, not paise). Gateway converts to paise internally. */
    @NotNull(message = "Amount is required")
    @DecimalMin(value = "1.00", message = "Amount must be at least ₹1")
    private BigDecimal amount;

    /** RAZORPAY | STRIPE | UPI */
    @NotBlank(message = "Gateway is required")
    private String gateway;
}