package com.superflick.modules.job.repository;

import com.superflick.modules.job.entity.JobSkill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public interface JobSkillRepository extends JpaRepository<JobSkill, UUID> {

    @Query("SELECT js FROM JobSkill js JOIN FETCH js.skill WHERE js.job.id = :jobId")
    List<JobSkill> findByJobIdWithSkill(@Param("jobId") UUID jobId);

    /**
     * Field in JobSkill entity is: private boolean required
     * JPQL path must be 'js.required' — NOT 'js.isRequired'
     * (same rule as aiExtracted, read, boosted, premium, etc.)
     */
    @Query("SELECT js FROM JobSkill js JOIN FETCH js.skill " +
            "WHERE js.job.id = :jobId AND js.required = true")
    List<JobSkill> findRequiredSkillsByJobId(@Param("jobId") UUID jobId);

    @Query("SELECT LOWER(js.skill.name) FROM JobSkill js WHERE js.job.id = :jobId")
    List<String> findSkillNamesByJobId(@Param("jobId") UUID jobId);

    boolean existsByJobIdAndSkillId(UUID jobId, UUID skillId);

    @Modifying
    @Transactional
    @Query("DELETE FROM JobSkill js WHERE js.job.id = :jobId")
    void deleteAllByJobId(@Param("jobId") UUID jobId);
}