package com.superflick.modules.job.mapper;

import com.superflick.modules.job.dto.JobCardResponse;
import com.superflick.modules.job.dto.JobRequest;
import com.superflick.modules.job.dto.JobResponse;
import com.superflick.modules.job.entity.Job;
import com.superflick.modules.job.entity.JobSkill;
import org.springframework.stereotype.Component;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.List;

@Component
public class JobMapper {

    public JobResponse toResponse(Job job) {
        if (job == null) return null;
        return JobResponse.builder()
                .id(job.getId())
                .hrId(job.getHr() != null ? job.getHr().getId() : null)
                .hrName(job.getHr() != null ? job.getHr().getFullName() : null)
                .companyId(job.getCompany() != null ? job.getCompany().getId() : null)
                .companyName(job.getCompany() != null ? job.getCompany().getName() : null)
                .companyLogoUrl(job.getCompany() != null ? job.getCompany().getLogoUrl() : null)
                .isCompanyPremium(job.getCompany() != null && job.getCompany().isPremium())
                .title(job.getTitle())
                .description(job.getDescription())
                .location(job.getLocation())
                .ctcMin(job.getCtcMin())
                .ctcMax(job.getCtcMax())
                .experienceRequired(job.getExperienceRequired())
                .shiftTimings(job.getShiftTimings())
                .isBoosted(job.isBoosted())
                .status(job.getStatus() != null ? job.getStatus().name() : null)
                .skills(mapSkillTags(job.getSkills()))   // instance call — no static context ✓
                .createdAt(job.getCreatedAt())
                .updatedAt(job.getUpdatedAt())
                .build();
    }

    public JobCardResponse toCardResponse(Job job) {
        if (job == null) return null;
        String desc    = job.getDescription();
        String preview = (desc != null && desc.length() > 200)
                ? desc.substring(0, 200) + "..." : desc;

        List<JobSkill> topSkills = job.getSkills() != null
                ? job.getSkills().stream().limit(6).toList() : List.of();

        return JobCardResponse.builder()
                .id(job.getId())
                .companyName(job.getCompany() != null ? job.getCompany().getName() : null)
                .companyLogoUrl(job.getCompany() != null ? job.getCompany().getLogoUrl() : null)
                .isCompanyPremium(job.getCompany() != null && job.getCompany().isPremium())
                .title(job.getTitle())
                .location(job.getLocation())
                .ctcMin(job.getCtcMin())
                .ctcMax(job.getCtcMax())
                .experienceRequired(job.getExperienceRequired())
                .shiftTimings(job.getShiftTimings())
                .isBoosted(job.isBoosted())
                .skills(mapSkillTags(topSkills))          // instance call ✓
                .descriptionPreview(preview)
                .postedAgo(timeAgo(job.getCreatedAt()))
                .build();
    }

    public void updateEntity(Job job, JobRequest req) {
        job.setTitle(req.getTitle());
        job.setDescription(req.getDescription());
        job.setLocation(req.getLocation());
        job.setCtcMin(req.getCtcMin());
        job.setCtcMax(req.getCtcMax());
        job.setExperienceRequired(req.getExperienceRequired());
        job.setShiftTimings(req.getShiftTimings());
    }

    // Non-static — called as instance method from this same class ✓
    private List<JobCardResponse.SkillTag> mapSkillTags(List<JobSkill> skills) {
        if (skills == null) return List.of();
        return skills.stream()
                .map(js -> JobCardResponse.SkillTag.builder()
                        .skillId(js.getSkill() != null ? js.getSkill().getId() : null)
                        .name(js.getSkill() != null ? js.getSkill().getName() : null)
                        .isRequired(js.isRequired())
                        .build())
                .toList();
    }

    private String timeAgo(LocalDateTime t) {
        if (t == null) return "";
        Duration d = Duration.between(t, LocalDateTime.now());
        if (d.toMinutes() < 60) return d.toMinutes() + " min ago";
        if (d.toHours() < 24)   return d.toHours() + " hours ago";
        return d.toDays() + " days ago";
    }
}