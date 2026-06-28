package com.superflick.modules.swipe.repository;

import com.superflick.modules.swipe.entity.SwipeAction;
import com.superflick.shared.enums.SwipeActionType;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Set;
import java.util.UUID;

public interface SwipeRepository extends JpaRepository<SwipeAction, UUID> {

    boolean existsByActorIdAndTargetId(UUID actorId, UUID targetId);

    boolean existsByActorIdAndTargetIdAndAction(UUID actorId, UUID targetId, SwipeActionType action);

    @Query("SELECT s.targetId FROM SwipeAction s WHERE s.actor.id = :actorId AND s.targetType = 'JOB'")
    Set<UUID> findSwipedJobIdsByCandidate(@Param("actorId") UUID actorId);

    @Query("SELECT s.targetId FROM SwipeAction s WHERE s.actor.id = :actorId")
    Set<UUID> findSwipedTargetIdsByActor(@Param("actorId") UUID actorId);

    @Query("SELECT s.targetId FROM SwipeAction s WHERE s.actor.id = :hrUserId " +
            "AND s.targetType = 'CANDIDATE' AND s.action = 'APPLY'")
    Set<UUID> findConsideredCandidateIdsByHR(@Param("hrUserId") UUID hrUserId);

    @Query("SELECT s.targetId FROM SwipeAction s WHERE s.actor.id = :hrUserId " +
            "AND s.targetType = 'CANDIDATE'")
    Set<UUID> findAllSwipedCandidateIdsByHR(@Param("hrUserId") UUID hrUserId);
}
 