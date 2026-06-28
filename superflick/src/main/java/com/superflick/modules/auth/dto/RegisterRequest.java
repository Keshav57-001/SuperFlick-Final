package com.superflick.modules.auth.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RegisterRequest {

    /**
     * Email address. Required for email-based registration.
     * Either email or mobile must be provided.
     */
    private String email;

    /**
     * Indian mobile number (10 digits, starts with 6–9).
     * Required for mobile-based registration.
     */
    @Pattern(regexp = "^[6-9]\\d{9}$", message = "Enter a valid 10-digit Indian mobile number")
    private String mobile;

    /**
     * Plain-text password. Minimum 8 characters with at least one
     * uppercase letter and one digit. Stored as BCrypt hash.
     */
    @NotBlank(message = "Password is required")
    @Size(min = 8, message = "Password must be at least 8 characters")
    @Pattern(
            regexp = "^(?=.*[A-Z])(?=.*\\d).+$",
            message = "Password must contain at least one uppercase letter and one digit"
    )
    private String password;

    /**
     * Role determines which profile setup page the user is redirected to
     * after OTP verification.
     * Allowed values: CANDIDATE | HR
     */
    @NotNull(message = "Role is required")
    private String role;
}