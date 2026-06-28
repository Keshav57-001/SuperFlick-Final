package com.superflick.modules.skill.repository;

import com.superflick.modules.skill.entity.Skill;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface SkillRepository extends JpaRepository<Skill, UUID> {

    Optional<Skill> findByNameIgnoreCase(String name);

    List<Skill> findAllByOrderByNameAsc();

    List<Skill> findByCategoryIgnoreCaseOrderByNameAsc(String category);

    /** Case-insensitive LIKE search — used by autocomplete dropdown. */
    @Query("SELECT s FROM Skill s WHERE LOWER(s.name) LIKE LOWER(CONCAT('%', :query, '%')) ORDER BY s.name")
    List<Skill> searchByName(@Param("query") String query);

    /** Returns all distinct category names sorted alphabetically. */
    @Query("SELECT DISTINCT s.category FROM Skill s ORDER BY s.category")
    List<String> findDistinctCategories();
}