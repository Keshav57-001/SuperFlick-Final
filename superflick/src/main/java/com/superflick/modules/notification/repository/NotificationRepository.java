
package com.superflick.modules.notification.repository;
import com.superflick.modules.notification.entity.Notification;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;
import java.util.UUID;
public interface NotificationRepository extends JpaRepository<Notification, UUID> {
    Page<Notification> findByUserIdOrderByCreatedAtDesc(UUID userId, Pageable pageable);
    /**
     * Field in Notification entity is: private boolean read;
     * Getter is: isRead()   — but Spring Data derives query from the FIELD name.
     *
     * Rule: for a boolean field named 'read', the derived method keyword is 'ReadFalse'
     *       NOT 'IsReadFalse' (that would require a field named 'isRead').
     *
     * Same rule applies everywhere in this project:
     *   field 'aiExtracted'  → method keyword: AiExtractedTrue / AiExtractedFalse
     *   field 'read'         → method keyword: ReadTrue / ReadFalse
     *   field 'boosted'      → method keyword: BoostedTrue / BoostedFalse
     *   field 'premium'      → method keyword: PremiumTrue / PremiumFalse
     */
    long countByUserIdAndReadFalse(UUID userId);
    @Modifying
    @Transactional
    @Query("UPDATE Notification n SET n.read = true WHERE n.userId = :userId")
    void markAllReadForUser(@Param("userId") UUID userId);
}
 