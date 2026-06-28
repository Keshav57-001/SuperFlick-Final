package com.superflick.modules.subscription.repository;

import com.superflick.modules.subscription.entity.Subscription;
import com.superflick.shared.enums.SubscriptionStatus;
import com.superflick.shared.enums.UserRole;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SubscriptionRepository extends JpaRepository<Subscription, UUID> {

    /** Used by SubscriptionService to get the latest subscription for a user. */
    Optional<Subscription> findTopByUserIdOrderByExpiresAtDesc(UUID userId);

    /** Used by SubscriptionService expiry cron job. */
    List<Subscription> findByStatusAndExpiresAtBefore(SubscriptionStatus status, LocalDateTime before);

    long countByStatus(SubscriptionStatus status);

    /**
     * Returns subscriptions whose owner has the given role.
     * Joins through the users table to filter by role.
     */
    @Query("SELECT s FROM Subscription s JOIN User u ON s.userId = u.id WHERE u.role = :role")
    Page<Subscription> findByUserRole(@Param("role") UserRole role, Pageable pageable);

    /**
     * Count active subscriptions for users of a given role.
     * Used by AdminService for premium stats.
     */
    @Query("SELECT COUNT(s) FROM Subscription s JOIN User u ON s.userId = u.id " +
            "WHERE u.role = :role AND s.status = 'ACTIVE' AND s.expiresAt > CURRENT_TIMESTAMP")
    long countActiveByUserRole(@Param("role") UserRole role);
}