package com.superflick.config;

import org.springframework.context.annotation.Configuration;

/**
 * DataConfig — intentionally minimal.
 *
 * DO NOT add @EnableJpaRepositories or @EnableRedisRepositories here.
 * Spring Boot's autoconfiguration handles JPA repository scanning automatically
 * via @EnableJpaRepositories declared on JpaRepositoriesRegistrar, which uses
 * the entityManagerFactory bean created by HibernateJpaAutoConfiguration.
 *
 * Adding a second @EnableJpaRepositories breaks the factory bean chain:
 *   DataConfig's @EnableJpaRepositories creates a SECOND JPA context
 *   that cannot find the shared entityManagerFactory → startup failure.
 *
 * The Redis "Could not safely identify store assignment" warnings are
 * suppressed via application.properties:
 *   spring.data.redis.repositories.enabled=false
 */
@Configuration
public class DataConfig {
    // Empty — Spring Boot autoconfiguration does everything needed.
}