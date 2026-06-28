package com.superflick.modules.candidate;

import com.superflick.modules.ai.AIService;
import com.superflick.modules.candidate.dto.CandidateCardResponse;
import com.superflick.modules.candidate.dto.CandidateProfileRequest;
import com.superflick.modules.candidate.dto.CandidateProfileResponse;
import com.superflick.modules.candidate.entity.CandidateProfile;
import com.superflick.modules.candidate.entity.CandidateSkill;
import com.superflick.modules.candidate.mapper.CandidateProfileMapper;
import com.superflick.modules.candidate.repository.CandidateRepository;
import com.superflick.modules.candidate.repository.CandidateSkillRepository;
import com.superflick.modules.file.FileService;
import com.superflick.modules.job.entity.Job;
import com.superflick.modules.job.repository.JobRepository;
import com.superflick.modules.matching.MatchingEngine;
import com.superflick.modules.skill.entity.Skill;
import com.superflick.modules.skill.repository.SkillRepository;
import com.superflick.modules.swipe.repository.SwipeRepository;
import com.superflick.modules.user.entity.User;
import com.superflick.modules.user.repository.UserRepository;
import com.superflick.shared.exception.BadRequestException;
import com.superflick.shared.exception.ConflictException;
import com.superflick.shared.exception.ForbiddenException;
import com.superflick.shared.exception.NotFoundException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.PageRequest;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Set;
import java.util.UUID;

@Slf4j
@Service
@Transactional(readOnly = true)
public class CandidateService {

    private final CandidateRepository     candidateRepo;
    private final CandidateSkillRepository candidateSkillRepo;
    private final UserRepository          userRepo;
    private final SkillRepository         skillRepo;
    private final JobRepository           jobRepo;
    private final FileService             fileService;
    private final AIService               aiService;
    private final MatchingEngine          matchingEngine;
    private final CandidateProfileMapper  candidateMapper;
    private final SwipeRepository         swipeRepo;

    public CandidateService(CandidateRepository candidateRepo,
                            CandidateSkillRepository candidateSkillRepo,
                            UserRepository userRepo,
                            SkillRepository skillRepo,
                            JobRepository jobRepo,
                            FileService fileService,
                            AIService aiService,
                            MatchingEngine matchingEngine,
                            CandidateProfileMapper candidateMapper,
                            SwipeRepository swipeRepo) {

        this.candidateRepo = candidateRepo;
        this.candidateSkillRepo = candidateSkillRepo;
        this.userRepo = userRepo;
        this.skillRepo = skillRepo;
        this.jobRepo = jobRepo;
        this.fileService = fileService;
        this.aiService = aiService;
        this.matchingEngine = matchingEngine;
        this.candidateMapper = candidateMapper;
        this.swipeRepo = swipeRepo;
    }

    @Transactional
    public CandidateProfileResponse createProfile(User user, CandidateProfileRequest req) {
        if (candidateRepo.existsByUserId(user.getId()))
            throw new ConflictException("Profile already exists");

        CandidateProfile profile = candidateMapper.toEntity(req);
        profile.setUser(user);

        linkSkills(profile, req.getSkillIds(), false);
        recalculateActivityScore(profile);
        CandidateProfile saved = candidateRepo.save(profile);

        user.setProfileComplete(true);
        userRepo.save(user);

        return candidateMapper.toResponse(saved);
    }

    public CandidateProfileResponse getProfile(UUID userId) {
        return candidateMapper.toResponse(findOrThrow(userId));
    }

    @Transactional
    public CandidateProfileResponse updateProfile(UUID userId, CandidateProfileRequest req) {
        CandidateProfile profile = findOrThrow(userId);
        candidateMapper.updateEntity(profile, req);
        candidateSkillRepo.deleteManualSkillsByCandidateId(profile.getId());
        linkSkills(profile, req.getSkillIds(), false);
        recalculateActivityScore(profile);
        return candidateMapper.toResponse(candidateRepo.save(profile));
    }

    @Transactional
    public CandidateProfileResponse updateSkills(UUID userId, List<UUID> skillIds) {
        if (skillIds == null || skillIds.isEmpty())
            throw new BadRequestException("At least one skill required");
        CandidateProfile profile = findOrThrow(userId);
        candidateSkillRepo.deleteManualSkillsByCandidateId(profile.getId());
        linkSkills(profile, skillIds.stream().map(UUID::toString).toList(), false);
        recalculateActivityScore(profile);
        return candidateMapper.toResponse(candidateRepo.save(profile));
    }

    @Transactional
    public CandidateProfileResponse uploadResume(UUID userId, MultipartFile file) {
        validateFile(file);
        CandidateProfile profile = findOrThrow(userId);
        String url = fileService.uploadFile(file, "resumes/" + userId + "/");
        profile.setResumeUrl(url);
        recalculateActivityScore(profile);
        CandidateProfile saved = candidateRepo.save(profile);
        aiService.extractAndSaveSkillsFromResume(userId, url);
        return candidateMapper.toResponse(saved);
    }

    public CandidateCardResponse getCandidateCard(UUID userId, UUID jobId) {
        CandidateProfile profile = findOrThrow(userId);
        Double matchScore = null;
        if (jobId != null) {
            Job job = jobRepo.findById(jobId).orElse(null);
            if (job != null) matchScore = (double) matchingEngine.calculate(profile, job).getScore();
        }
        return candidateMapper.toCardResponse(profile, matchScore);
    }

    /**
     * Returns the next candidate card for HR to swipe on, scored against the given job.
     * Excludes candidates the HR has already swiped (any action) for the CANDIDATE target type.
     */
    public CandidateCardResponse getNextCandidateForJob(UUID hrUserId, UUID jobId) {
        Job job = jobRepo.findById(jobId)
                .orElseThrow(() -> new NotFoundException("Job not found"));

        if (!job.getHr().getUser().getId().equals(hrUserId)) {
            throw new ForbiddenException("Not your job");
        }

        Set<UUID> excludedIds = swipeRepo.findAllSwipedCandidateIdsByHR(hrUserId);
        if (excludedIds.isEmpty()) excludedIds = Set.of(UUID.randomUUID());

        List<CandidateProfile> candidates = candidateRepo.findFeedForHR(
                excludedIds, PageRequest.of(0, 1));

        if (candidates.isEmpty()) return null;

        CandidateProfile profile = candidates.get(0);
        double score = matchingEngine.calculate(profile, job).getScore();
        return candidateMapper.toCardResponse(profile, score);
    }

    @Transactional
    public void deleteAccount(UUID userId) {
        userRepo.deleteById(userId);
    }

    // ── Helpers ───────────────────────────────────────────────
    private CandidateProfile findOrThrow(UUID userId) {
        return candidateRepo.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("Candidate profile not found"));
    }

    private void linkSkills(CandidateProfile profile, List<String> skillIds, boolean aiExtracted) {
        if (skillIds == null) return;
        for (String idStr : skillIds) {
            UUID skillId;
            try { skillId = UUID.fromString(idStr); } catch (Exception e) { continue; }
            if (candidateSkillRepo.existsByCandidateIdAndSkillId(profile.getId(), skillId)) continue;
            Skill skill = skillRepo.findById(skillId).orElse(null);
            if (skill == null) continue;

            CandidateSkill cs = new CandidateSkill();
            cs.setCandidate(profile);
            cs.setSkill(skill);
            cs.setProficiency("INTERMEDIATE");
            cs.setAiExtracted(aiExtracted);
            profile.getSkills().add(cs);
        }
    }

    private void recalculateActivityScore(CandidateProfile p) {
        int score = 0;
        if (p.getResumeUrl() != null && !p.getResumeUrl().isBlank()) score += 30;
        if (p.getSkills() != null && p.getSkills().size() >= 3)      score += 25;
        if (p.getAbout() != null && !p.getAbout().isBlank())         score += 15;
        if (p.getLinkedinUrl() != null && !p.getLinkedinUrl().isBlank()) score += 10;
        if (p.getExpectedCtc() != null)                              score += 10;
        if (p.getGithubUrl() != null && !p.getGithubUrl().isBlank()) score += 5;
        if (p.getDreamCompany() != null && !p.getDreamCompany().isBlank()) score += 5;
        p.setActivityScore(Math.min(score, 100));
    }

    private void validateFile(MultipartFile file) {
        if (file == null || file.isEmpty())
            throw new BadRequestException("File is required");
        String ct = file.getContentType();
        if (ct == null || (!ct.equals("application/pdf")
                && !ct.equals("application/msword")
                && !ct.contains("wordprocessingml")))
            throw new BadRequestException("Only PDF and DOC/DOCX files are accepted");
        if (file.getSize() > 10L * 1024 * 1024)
            throw new BadRequestException("File must be under 10 MB");
    }
}