package com.superflick.modules.job.repository;
import com.superflick.modules.job.entity.Job;
import com.superflick.modules.hr.entity.HRProfile;
import com.superflick.shared.enums.JobStatus;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Set;
import java.util.UUID;
public interface JobRepository extends JpaRepository<Job, UUID> {
    List<Job> findByHr(HRProfile hr);
    long countByStatus(JobStatus status);
    /**
     * Fixed Candidate Feed Query:
     * Utilizes a safe coalesce statement fallback to ensure new candidates
     * with an empty swipe history set don't break the query syntax.
     */
    @Query("SELECT j FROM Job j " +
            "WHERE j.status = 'ACTIVE' " +
            "AND (COALESCE(:excludedIds, NULL) IS NULL OR j.id NOT IN :excludedIds) " +
            "ORDER BY j.boosted DESC, j.createdAt DESC")
    List<Job> findFeedForCandidate(@Param("excludedIds") Set<UUID> excludedIds, Pageable pageable);
    @Query("SELECT j FROM Job j " +
            "WHERE j.status = 'ACTIVE' " +
            "ORDER BY j.boosted DESC, j.createdAt DESC")
    List<Job> findAllActiveFeed(Pageable pageable);
    @Query("SELECT j FROM Job j WHERE j.createdAt > :after AND j.status = 'ACTIVE'")
    List<Job> findJobsPostedAfter(@Param("after") LocalDateTime after);
    List<Job> findByStatus(JobStatus status, Pageable pageable);
    @Query("SELECT j FROM Job j WHERE j.hr.id = :hrId AND j.status = 'ACTIVE' ORDER BY j.createdAt DESC")
    List<Job> findActiveJobsByHr(@Param("hrId") UUID hrId);
    @Query("SELECT j FROM Job j WHERE j.hr.user.id = :userId ORDER BY j.createdAt DESC")
    List<Job> findByHrUserId(@Param("userId") UUID userId);
    long countByHrUserIdAndStatus(UUID userId, JobStatus status);
}