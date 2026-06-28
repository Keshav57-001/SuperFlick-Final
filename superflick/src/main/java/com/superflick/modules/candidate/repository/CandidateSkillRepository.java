package com.superflick.modules.candidate.repository;

import com.superflick.modules.candidate.entity.CandidateSkill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

public interface CandidateSkillRepository extends JpaRepository<CandidateSkill, UUID> {

    @Query("SELECT cs FROM CandidateSkill cs JOIN FETCH cs.skill " +
            "WHERE cs.candidate.id = :candidateId")
    List<CandidateSkill> findByCandidateIdWithSkill(@Param("candidateId") UUID candidateId);

    boolean existsByCandidateIdAndSkillId(UUID candidateId, UUID skillId);

    /**
     * Field name in the entity is 'aiExtracted' (not 'isAiExtracted').
     * Hibernate resolves JPQL paths by field name, not by getter name.
     * Getter isAiExtracted() is fine for Java code but the JPQL must use
     * the actual field name: cs.aiExtracted
     */
    @Query("SELECT cs FROM CandidateSkill cs JOIN FETCH cs.skill " +
            "WHERE cs.candidate.id = :candidateId AND cs.aiExtracted = true")
    List<CandidateSkill> findAiExtractedSkills(@Param("candidateId") UUID candidateId);

    @Modifying
    @Transactional
    @Query("DELETE FROM CandidateSkill cs WHERE cs.candidate.id = :candidateId")
    void deleteAllByCandidateId(@Param("candidateId") UUID candidateId);

    @Modifying
    @Transactional
    @Query("DELETE FROM CandidateSkill cs WHERE cs.candidate.id = :candidateId " +
            "AND cs.aiExtracted = false")
    void deleteManualSkillsByCandidateId(@Param("candidateId") UUID candidateId);

    @Query("SELECT COUNT(cs) FROM CandidateSkill cs WHERE cs.candidate.id = :candidateId")
    long countByCandidateId(@Param("candidateId") UUID candidateId);

    @Query("SELECT LOWER(cs.skill.name) FROM CandidateSkill cs " +
            "WHERE cs.candidate.id = :candidateId")
    List<String> findSkillNamesByCandidateId(@Param("candidateId") UUID candidateId);
}