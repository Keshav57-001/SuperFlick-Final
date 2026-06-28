package com.superflick.modules.skill;

import com.superflick.modules.skill.dto.SkillResponse;
import com.superflick.modules.skill.entity.Skill;
import com.superflick.modules.skill.repository.SkillRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Service
@RequiredArgsConstructor
@Transactional(readOnly = true)
public class SkillService {

    private final SkillRepository skillRepo;

    @Cacheable("skills-all")
    public List<SkillResponse> getAllSkills() {
        return skillRepo.findAllByOrderByNameAsc()
                .stream().map(this::toResponse).toList();
    }

    @Cacheable(value = "skills-by-category", key = "#category")
    public List<SkillResponse> getByCategory(String category) {
        return skillRepo.findByCategoryIgnoreCaseOrderByNameAsc(category)
                .stream().map(this::toResponse).toList();
    }

    public List<SkillResponse> search(String query) {
        return skillRepo.searchByName(query.trim())
                .stream().limit(20).map(this::toResponse).toList();
    }

    @Cacheable("skill-categories")
    public List<String> getCategories() {
        return skillRepo.findDistinctCategories();
    }

    // ── Private helper — no static mapper needed ──────────────
    private SkillResponse toResponse(Skill skill) {
        return SkillResponse.builder()
                .id(skill.getId())
                .name(skill.getName())
                .category(skill.getCategory())
                .build();
    }
}