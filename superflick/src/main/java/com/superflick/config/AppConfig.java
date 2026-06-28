package com.superflick.config;

import org.springframework.cache.annotation.EnableCaching;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.web.client.RestTemplate;

/**
 * General application configuration.
 * Enables:
 *   - JPA Auditing (BaseEntity.createdAt / updatedAt auto-population)
 *   - Spring Cache (for SkillService skill list caching)
 *   - RestTemplate bean (used by AIService, SmsService, OAuthProviders)
 */
@Configuration
@EnableJpaAuditing
@EnableCaching
public class AppConfig {

    @Bean
    public RestTemplate restTemplate() {
        return new RestTemplate();
    }
}