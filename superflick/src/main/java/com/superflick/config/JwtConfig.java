package com.superflick.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.context.annotation.Configuration;

@Data
@Configuration
@ConfigurationProperties(prefix = "app.jwt")
public class JwtConfig {

    /**
     * Secret key used for HMAC-SHA256 signing.
     * Must be at least 256 bits (32 chars). Loaded from app.jwt.secret in application.yml.
     * Example: app.jwt.secret=${JWT_SECRET}
     */
    private String secret;

    /**
     * Token validity in milliseconds.
     * Default: 86400000 (24 hours).
     * Example: app.jwt.expiry-ms=86400000
     */
    private long expiryMs = 86_400_000L;

    /**
     * Refresh token validity in milliseconds.
     * Default: 604800000 (7 days).
     * Example: app.jwt.refresh-expiry-ms=604800000
     */
    private long refreshExpiryMs = 604_800_000L;

    /**
     * Token prefix used in the Authorization header.
     * Example: Authorization: Bearer <token>
     */
    private String tokenPrefix = "Bearer ";

    /**
     * The Authorization header name.
     */
    private String headerName = "Authorization";
}