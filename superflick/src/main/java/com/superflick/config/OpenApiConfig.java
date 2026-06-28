package com.superflick.config;

import io.swagger.v3.oas.models.Components;
import io.swagger.v3.oas.models.OpenAPI;
import io.swagger.v3.oas.models.info.Contact;
import io.swagger.v3.oas.models.info.Info;
import io.swagger.v3.oas.models.info.License;
import io.swagger.v3.oas.models.security.SecurityRequirement;
import io.swagger.v3.oas.models.security.SecurityScheme;
import io.swagger.v3.oas.models.servers.Server;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class OpenApiConfig {

    private static final String SECURITY_SCHEME_NAME = "BearerAuth";

    @Value("${app.openapi.server-url:http://localhost:8080}")
    private String serverUrl;

    @Bean
    public OpenAPI superFlickOpenAPI() {
        return new OpenAPI()
                .info(buildInfo())
                .servers(List.of(
                        new Server().url(serverUrl).description("Current server"),
                        new Server().url("https://api.superflick.com").description("Production"),
                        new Server().url("http://localhost:8080").description("Local development")
                ))
                // Global security requirement: every endpoint requires Bearer token by default.
                // Individual endpoints can override this with @SecurityRequirements({})
                .addSecurityItem(new SecurityRequirement().addList(SECURITY_SCHEME_NAME))
                .components(new Components()
                        .addSecuritySchemes(SECURITY_SCHEME_NAME, buildSecurityScheme())
                );
    }

    private Info buildInfo() {
        return new Info()
                .title("SuperFlick API")
                .version("1.0.0")
                .description("""
                        SuperFlick – Swipe-based Job Discovery Platform API.
                        
                        **Authentication:** All protected endpoints require a JWT Bearer token.
                        Obtain a token via `POST /api/v1/auth/login` or `POST /api/v1/auth/otp/verify`.
                        Pass it as: `Authorization: Bearer <token>`
                        
                        **Roles:** CANDIDATE | HR | ADMIN | SUPER_ADMIN
                        """)
                .contact(new Contact()
                        .name("SuperFlick Engineering")
                        .email("engineering@superflick.com")
                        .url("https://superflick.com"))
                .license(new License()
                        .name("Private — All rights reserved")
                        .url("https://superflick.com/terms"));
    }

    private SecurityScheme buildSecurityScheme() {
        return new SecurityScheme()
                .name(SECURITY_SCHEME_NAME)
                .type(SecurityScheme.Type.HTTP)
                .scheme("bearer")
                .bearerFormat("JWT")
                .description("Enter the JWT token obtained from the login endpoint.");
    }
}