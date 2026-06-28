package com.superflick.modules.chat.repository;

import com.superflick.modules.chat.entity.Message;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

public interface MessageRepository extends JpaRepository<Message, UUID> {

    Page<Message> findByMatchIdOrderBySentAtDesc(UUID matchId, Pageable pageable);

    Optional<Message> findTopByMatchIdOrderBySentAtDesc(UUID matchId);

    @Query("SELECT COUNT(m) FROM Message m WHERE m.match.id = :matchId " +
            "AND m.senderId != :userId AND m.read = false")
    int countUnreadForRecipient(@Param("userId") UUID userId,
                                @Param("matchId") UUID matchId);

    @Modifying
    @Transactional
    @Query("UPDATE Message m SET m.read = true WHERE m.match.id = :matchId " +
            "AND m.senderId != :userId")
    void markAllReadForRecipient(@Param("userId") UUID userId,
                                 @Param("matchId") UUID matchId);
}