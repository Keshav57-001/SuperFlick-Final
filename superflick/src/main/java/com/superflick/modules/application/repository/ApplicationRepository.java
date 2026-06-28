package com.superflick.modules.application.repository;

import com.superflick.modules.application.entity.Application;
import com.superflick.modules.candidate.entity.CandidateProfile;
import com.superflick.modules.job.entity.Job;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;
import java.util.UUID;

public interface ApplicationRepository extends JpaRepository<Application, UUID> {

    Page<Application> findByCandidate(CandidateProfile candidate, Pageable pageable);

    boolean existsByCandidateAndJob(CandidateProfile candidate, Job job);

    @Query("SELECT a FROM Application a WHERE a.candidate.user.id = :candidateUserId " +
            "AND a.job.hr.user.id = :hrUserId")
    Optional<Application> findByCandidateUserIdAndHRUserId(
            @Param("candidateUserId") UUID candidateUserId,
            @Param("hrUserId") UUID hrUserId);

    @Query("SELECT a FROM Application a WHERE a.job.id = :jobId " +
            "AND (:stage IS NULL OR CAST(a.stage AS string) = :stage) " +
            "AND (:applyType IS NULL OR CAST(a.applyType AS string) = :applyType) " +
            "ORDER BY a.matchScore DESC NULLS LAST")
    Page<Application> findByJobIdWithFilters(@Param("jobId") UUID jobId,
                                             @Param("stage") String stage,
                                             @Param("applyType") String applyType,
                                             Pageable pageable);

    @Query("SELECT a FROM Application a WHERE " +
            "(:applyType IS NULL OR CAST(a.applyType AS string) = :applyType) " +
            "AND (:stage IS NULL OR CAST(a.stage AS string) = :stage)")
    Page<Application> findAllWithFilters(@Param("applyType") String applyType,
                                         @Param("stage") String stage,
                                         Pageable pageable);

    @Query("SELECT COUNT(a) FROM Application a WHERE CAST(a.applyType AS string) = :applyType")
    long countByApplyType(@Param("applyType") String applyType);
}