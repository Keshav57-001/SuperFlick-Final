package com.superflick.modules.skill;

import com.superflick.modules.skill.dto.SkillResponse;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/v1/skills")
@RequiredArgsConstructor
@Tag(name = "Skills", description = "Skill master data — used to populate candidate and job skill dropdowns")
public class SkillController {

    private final SkillService skillService;

    /**
     * GET /api/v1/skills
     * Returns all skills. No auth required — used to populate registration dropdowns.
     */
    @GetMapping
    @Operation(summary = "Get all skills")
    public ResponseEntity<List<SkillResponse>> getAllSkills() {
        return ResponseEntity.ok(skillService.getAllSkills());
    }

    /**
     * GET /api/v1/skills?category=Backend
     * Filters skills by category. Useful for stepped skill selection UI.
     */
    @GetMapping(params = "category")
    @Operation(summary = "Get skills by category")
    public ResponseEntity<List<SkillResponse>> getByCategory(@RequestParam String category) {
        return ResponseEntity.ok(skillService.getByCategory(category));
    }

    /**
     * GET /api/v1/skills/search?q=java
     * Fuzzy search by name — powers the skill autocomplete/multi-select.
     */
    @GetMapping("/search")
    @Operation(summary = "Search skills by name")
    public ResponseEntity<List<SkillResponse>> search(@RequestParam String q) {
        return ResponseEntity.ok(skillService.search(q));
    }

    /**
     * GET /api/v1/skills/categories
     * Returns all distinct category names for building category tabs.
     */
    @GetMapping("/categories")
    @Operation(summary = "Get all skill categories")
    public ResponseEntity<List<String>> getCategories() {
        return ResponseEntity.ok(skillService.getCategories());
    }
}