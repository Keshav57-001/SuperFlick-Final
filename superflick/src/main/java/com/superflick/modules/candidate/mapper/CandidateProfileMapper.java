package com.superflick.modules.candidate.mapper;

import com.superflick.modules.candidate.dto.CandidateCardResponse;
import com.superflick.modules.candidate.dto.CandidateProfileRequest;
import com.superflick.modules.candidate.dto.CandidateProfileResponse;
import com.superflick.modules.candidate.entity.CandidateProfile;
import com.superflick.modules.candidate.entity.CandidateSkill;
import com.superflick.modules.user.entity.User;
import org.springframework.stereotype.Component;

import java.time.LocalDate;
import java.util.List;

@Component
public class CandidateProfileMapper {

    public CandidateProfileResponse toResponse(CandidateProfile profile) {
        if (profile == null) return null;
        User user = profile.getUser();
        return CandidateProfileResponse.builder()
                .id(profile.getId())
                .userId(user != null ? user.getId() : null)
                .fullName(profile.getFullName())
                .email(user != null ? user.getEmail() : null)
                .phone(user != null ? user.getPhone() : null)
                .dob(profile.getDob() != null ? profile.getDob().toString() : null)
                .gender(profile.getGender())
                .currentLocation(profile.getCurrentLocation())
                .preferredLocation(profile.getPreferredLocation())
                .resumeUrl(profile.getResumeUrl())
                .linkedinUrl(profile.getLinkedinUrl())
                .githubUrl(profile.getGithubUrl())
                .about(profile.getAbout())
                .isEmployed(profile.isEmployed())
                .currentCompany(profile.getCurrentCompany())
                .currentRole(profile.getCurrentRole())
                .experienceYears(profile.getExperienceYears())
                .experienceMonths(profile.getExperienceMonths())
                .currentCtc(profile.getCurrentCtc())
                .expectedCtc(profile.getExpectedCtc())
                .noticePeriod(profile.getNoticePeriod())
                .highestQualification(profile.getHighestQualification())
                .fieldOfStudy(profile.getFieldOfStudy())
                .collegeName(profile.getCollegeName())
                .passingYear(profile.getPassingYear())
                .internshipExperience(profile.getInternshipExperience())
                .dreamCompany(profile.getDreamCompany())
                .preferredJobTypes(profile.getPreferredJobTypes())
                .preferredIndustry(profile.getPreferredIndustry())
                .willingToRelocate(profile.isWillingToRelocate())
                .preferredJobRole(profile.getPreferredJobRole())
                .skills(mapSkillTags(profile.getSkills()))   // instance call ✓
                .isPremium(profile.isPremium())
                .premiumUntil(profile.getPremiumUntil())
                .activityScore(profile.getActivityScore())
                .createdAt(profile.getCreatedAt())
                .updatedAt(profile.getUpdatedAt())
                .build();
    }

    public CandidateCardResponse toCardResponse(CandidateProfile profile, Double matchScore) {
        if (profile == null) return null;
        List<CandidateSkill> top8 = profile.getSkills() != null
                ? profile.getSkills().stream().limit(8).toList() : List.of();
        return CandidateCardResponse.builder()
                .userId(profile.getUser() != null ? profile.getUser().getId() : null)
                .candidateProfileId(profile.getId())
                .fullName(profile.getFullName())
                .currentRole(profile.getCurrentRole())
                .currentCompany(profile.getCurrentCompany())
                .experienceYears(profile.getExperienceYears())
                .experienceMonths(profile.getExperienceMonths())
                .experienceFormatted(formatExp(profile.getExperienceYears(), profile.getExperienceMonths()))
                .noticePeriod(profile.getNoticePeriod())
                .willingToRelocate(profile.isWillingToRelocate())
                .currentLocation(profile.getCurrentLocation())
                .preferredLocation(profile.getPreferredLocation())
                .expectedCtc(profile.getExpectedCtc())
                .skills(mapSkillTags(top8))                  // instance call ✓
                .resumeUrl(profile.getResumeUrl())
                .about(profile.getAbout())
                .isPremium(profile.isPremium())
                .activityScore(profile.getActivityScore())
                .matchScore(matchScore)
                .build();
    }

    public CandidateCardResponse toCardResponse(CandidateProfile profile) {
        return toCardResponse(profile, null);
    }

    /**
     * Maps request → new entity (no user arg — user is set in CandidateService).
     */
    public CandidateProfile toEntity(CandidateProfileRequest req) {
        CandidateProfile p = new CandidateProfile();
        apply(p, req);
        return p;
    }

    public void updateEntity(CandidateProfile profile, CandidateProfileRequest req) {
        apply(profile, req);
    }

    // ── Private helpers — all instance methods ────────────────
    private void apply(CandidateProfile p, CandidateProfileRequest req) {
        p.setFullName(req.getFullName());
        p.setGender(req.getGender());
        if (req.getDob() != null && !req.getDob().isBlank())
            p.setDob(LocalDate.parse(req.getDob()));
        p.setCurrentLocation(req.getCurrentLocation());
        p.setPreferredLocation(req.getPreferredLocation());
        p.setLinkedinUrl(req.getLinkedinUrl());
        p.setGithubUrl(req.getGithubUrl());
        p.setAbout(req.getAbout());
        p.setEmployed(req.isEmployed());
        p.setCurrentCompany(req.getCurrentCompany());
        p.setCurrentRole(req.getCurrentRole());
        p.setExperienceYears(req.getExperienceYears());
        p.setExperienceMonths(req.getExperienceMonths());
        p.setCurrentCtc(req.getCurrentCtc());
        p.setExpectedCtc(req.getExpectedCtc());
        p.setNoticePeriod(req.getNoticePeriod());
        p.setHighestQualification(req.getHighestQualification());
        p.setFieldOfStudy(req.getFieldOfStudy());
        p.setCollegeName(req.getCollegeName());
        p.setPassingYear(req.getPassingYear());
        p.setInternshipExperience(req.getInternshipExperience());
        p.setDreamCompany(req.getDreamCompany());
        p.setPreferredJobTypes(req.getPreferredJobTypes());
        p.setPreferredIndustry(req.getPreferredIndustry());
        p.setWillingToRelocate(req.isWillingToRelocate());
        p.setPreferredJobRole(req.getPreferredJobRole());
    }

    private List<CandidateCardResponse.SkillTag> mapSkillTags(List<CandidateSkill> skills) {
        if (skills == null) return List.of();
        return skills.stream()
                .map(cs -> CandidateCardResponse.SkillTag.builder()
                        .skillId(cs.getSkill() != null ? cs.getSkill().getId() : null)
                        .name(cs.getSkill() != null ? cs.getSkill().getName() : null)
                        .category(cs.getSkill() != null ? cs.getSkill().getCategory() : null)
                        .proficiency(cs.getProficiency())
                        .aiExtracted(cs.isAiExtracted())
                        .build())
                .toList();
    }

    private String formatExp(Integer years, Integer months) {
        if ((years == null || years == 0) && (months == null || months == 0)) return "Fresher";
        StringBuilder sb = new StringBuilder();
        if (years  != null && years  > 0) sb.append(years).append("y ");
        if (months != null && months > 0) sb.append(months).append("m");
        return sb.toString().trim();
    }
}