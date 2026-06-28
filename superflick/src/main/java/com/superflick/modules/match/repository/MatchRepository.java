package com.superflick.modules.match.repository;

import com.superflick.modules.match.entity.Match;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import java.util.*;

public interface MatchRepository extends JpaRepository<Match, UUID> {
    boolean existsByCandidateUserIdAndHrUserIdAndJobId(UUID candidateUserId, UUID hrUserId, UUID jobId);

    @Query("SELECT m FROM Match m WHERE m.candidateUserId = :userId OR m.hrUserId = :userId")
    List<Match> findByParticipant(UUID userId);
}
