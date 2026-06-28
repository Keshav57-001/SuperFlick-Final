package com.superflick.modules.candidate.repository;
import com.superflick.modules.candidate.entity.CandidateProfile;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;
public interface CandidateRepository extends JpaRepository<CandidateProfile, UUID> {
    Optional<CandidateProfile> findByUserId(UUID userId);
    boolean existsByUserId(UUID userId);
    @Query("SELECT c FROM CandidateProfile c WHERE c.premium = true " +
            "AND (c.premiumUntil IS NULL OR c.premiumUntil > CURRENT_TIMESTAMP)")
    List<CandidateProfile> findAllActivePremium();
    @Query("SELECT c FROM CandidateProfile c WHERE c.user.id IN :userIds")
    Page<CandidateProfile> findByUserIds(@Param("userIds") Set<UUID> userIds, Pageable pageable);
    /**
     * Returns candidates the HR hasn't swiped on yet, ordered by activityScore.
     * The jobId param is intentionally NOT used in the query — candidates are not
     * linked to a specific job. Job-skill matching is done in the MatchingEngine
     * after fetch, not in SQL.
     */
    @Query("SELECT c FROM CandidateProfile c " +
            "WHERE c.user.id NOT IN :excludedIds " +
            "ORDER BY c.activityScore DESC")
    List<CandidateProfile> findFeedForHR(@Param("excludedIds") Set<UUID> excludedIds,
                                         Pageable pageable);
}
 