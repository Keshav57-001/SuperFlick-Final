package com.superflick.modules.matching;

import com.superflick.modules.candidate.entity.CandidateProfile;
import com.superflick.modules.job.entity.Job;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class MatchingEngine {

    private final SkillSimilarityService skillSimilarity;

    public MatchScoreResult calculate(CandidateProfile candidate, Job job) {
        int score            = 0;
        int matchedSkillCount = 0;
        List<String> reasons = new ArrayList<>();

        // ── 1. Skill matching (max 40 pts) ───────────────────
        List<String> candidateSkillNames = (candidate.getSkills() == null)
                ? List.of()
                : candidate.getSkills().stream()
                .filter(cs -> cs.getSkill() != null)
                .map(cs -> cs.getSkill().getName().toLowerCase())
                .collect(Collectors.toList());

        List<String> jobSkillNames = (job.getSkills() == null)
                ? List.of()
                : job.getSkills().stream()
                .filter(js -> js.getSkill() != null)
                .map(js -> js.getSkill().getName().toLowerCase())
                .collect(Collectors.toList());

        for (String jobSkill : jobSkillNames) {
            boolean direct  = candidateSkillNames.contains(jobSkill);
            boolean related = !direct && skillSimilarity.isRelated(jobSkill, candidateSkillNames);
            if (direct || related) {
                matchedSkillCount++;
                score += direct ? 8 : 5;
                reasons.add("Skill: " + jobSkill);
            }
        }
        score = Math.min(score, 40);

        // ── 2. Experience matching (20 pts) ──────────────────
        int expReq      = job.getExperienceRequired() != null ? job.getExperienceRequired() : 0;
        int candidateExp = candidate.getExperienceYears() != null ? candidate.getExperienceYears() : 0;
        if (candidateExp >= expReq)          { score += 20; reasons.add("Experience match"); }
        else if (candidateExp >= expReq - 1) { score += 10; reasons.add("Near experience match"); }

        // ── 3. Location matching (20 pts) ────────────────────
        String preferred = candidate.getPreferredLocation();
        String jobLoc    = job.getLocation();
        if (preferred != null && jobLoc != null
                && (preferred.equalsIgnoreCase(jobLoc)
                || jobLoc.toLowerCase().contains("remote")
                || preferred.toLowerCase().contains("remote"))) {
            score += 20;
            reasons.add("Location match");
        }

        // ── 4. Salary matching (20 pts) ──────────────────────
        BigDecimal expectedCtc = candidate.getExpectedCtc();
        BigDecimal jobCtcMax   = job.getCtcMax();
        if (expectedCtc != null && jobCtcMax != null) {
            if (expectedCtc.compareTo(jobCtcMax) <= 0) {
                score += 20;
                reasons.add("Salary match");
            } else if (expectedCtc.compareTo(jobCtcMax.multiply(BigDecimal.valueOf(1.1))) <= 0) {
                score += 10;
                reasons.add("Near salary match");
            }
        }

        return MatchScoreResult.builder()
                .score(Math.min(score, 100))
                .matchedSkillCount(matchedSkillCount)
                .meetsAutoApplyCriteria(matchedSkillCount >= 4
                        && candidateExp >= expReq
                        && score >= 60)
                .reasons(reasons)
                .build();
    }
}