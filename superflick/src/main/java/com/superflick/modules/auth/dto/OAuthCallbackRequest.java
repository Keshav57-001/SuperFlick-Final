package com.superflick.modules.auth.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class OAuthCallbackRequest {

    /**
     * OAuth provider name in uppercase.
     * Allowed values: GOOGLE | MICROSOFT | GITHUB | LINKEDIN
     */
    @NotBlank(message = "Provider is required")
    private String provider;

    /**
     * The authorization code returned by the OAuth provider's redirect.
     * The backend exchanges this code for an access token and user profile.
     */
    @NotBlank(message = "Authorization code is required")
    private String code;

    /**
     * The role the user is registering as.
     * Required on first-time OAuth login so we know which profile to create.
     * Allowed values: CANDIDATE | HR
     */
    private String role;
}