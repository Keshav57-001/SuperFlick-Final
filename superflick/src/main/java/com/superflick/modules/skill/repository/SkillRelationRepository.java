package com.superflick.modules.skill.repository;

import com.superflick.modules.skill.entity.SkillRelation;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import java.util.List;
import java.util.UUID;

public interface SkillRelationRepository extends JpaRepository<SkillRelation, UUID> {

    /**
     * Returns all skills related to the given skill, ordered by similarity descending.
     * Used by the matching engine to check indirect skill compatibility.
     */
    @Query("SELECT sr FROM SkillRelation sr JOIN FETCH sr.relatedSkill " +
            "WHERE sr.skill.id = :skillId ORDER BY sr.similarityScore DESC")
    List<SkillRelation> findRelatedSkillsBySkillId(@Param("skillId") UUID skillId);

    /**
     * Returns the lowercase names of all skills related to the given skill name.
     * Avoids loading full entities — used for bulk matching.
     */
    @Query("SELECT LOWER(sr.relatedSkill.name) FROM SkillRelation sr " +
            "WHERE LOWER(sr.skill.name) = LOWER(:skillName)")
    List<String> findRelatedSkillNamesByName(@Param("skillName") String skillName);

    /**
     * Checks whether two skills have a defined similarity relationship.
     */
    boolean existsBySkillIdAndRelatedSkillId(UUID skillId, UUID relatedSkillId);
}