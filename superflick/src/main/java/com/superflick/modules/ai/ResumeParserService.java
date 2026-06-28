package com.superflick.modules.ai;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.superflick.modules.candidate.entity.CandidateProfile;
import com.superflick.modules.candidate.entity.CandidateSkill;
import com.superflick.modules.candidate.repository.CandidateRepository;
import com.superflick.modules.skill.entity.Skill;
import com.superflick.modules.skill.repository.SkillRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.pdfbox.Loader;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;

import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

/**
 * Standalone resume parser — kept for backwards compatibility.
 * New code should use AIService.extractAndSaveSkillsFromResume() directly.
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ResumeParserService {

    private final CandidateRepository candidateRepo;
    private final SkillRepository     skillRepo;
    private final RestTemplate        restTemplate;
    private final ObjectMapper        objectMapper;

    @Value("${openai.api.key:}")
    private String openAiKey;

    @Async
    @Transactional
    public void extractAndSaveSkills(UUID userId, String resumeUrl) {
        log.info("Resume parsing started: userId={}", userId);
        try {
            String text = extractText(resumeUrl);
            if (text == null || text.isBlank()) return;
            List<String> names = callOpenAi(text);
            persist(userId, names);
        } catch (Exception ex) {
            log.error("Resume parsing failed for userId={}: {}", userId, ex.getMessage(), ex);
        }
    }

    private String extractText(String url) {
        try {
            byte[] bytes;
            try (InputStream is = new URL(url).openStream()) {
                bytes = is.readAllBytes();
            }
            // PDFBox 3.x: use Loader.loadPDF(byte[]) — PDDocument.load() was removed
            try (PDDocument doc = Loader.loadPDF(bytes)) {
                return new PDFTextStripper().getText(doc);
            }
        } catch (Exception ex) {
            log.error("PDF text extraction failed: {}", ex.getMessage());
            return null;
        }
    }

    @SuppressWarnings("unchecked")
    private List<String> callOpenAi(String text) throws Exception {
        if (openAiKey == null || openAiKey.isBlank()) return List.of();
        String prompt = "Extract technical skills from this resume. Return JSON array only: " + text;
        HttpHeaders h = new HttpHeaders();
        h.setContentType(MediaType.APPLICATION_JSON);
        h.setBearerAuth(openAiKey);
        Map<String, Object> body = Map.of(
                "model", "gpt-4o-mini", "max_tokens", 300, "temperature", 0.1,
                "messages", List.of(Map.of("role", "user", "content", prompt)));
        ResponseEntity<Map> r = restTemplate.postForEntity(
                "https://api.openai.com/v1/chat/completions",
                new HttpEntity<>(body, h), Map.class);
        List<Map<String, Object>> choices = (List<Map<String, Object>>) r.getBody().get("choices");
        String content = (String) ((Map<String, Object>) choices.get(0).get("message")).get("content");
        String cleaned = content.trim().replaceAll("^```json|^```|```$", "").trim();
        return objectMapper.readValue(cleaned, new TypeReference<>() {});
    }

    @Transactional
    private void persist(UUID userId, List<String> names) {
        CandidateProfile cp = candidateRepo.findByUserId(userId).orElse(null);
        if (cp == null) return;
        for (String name : names) {
            Optional<Skill> opt = skillRepo.findByNameIgnoreCase(name.trim());
            opt.ifPresent(skill -> {
                boolean exists = cp.getSkills().stream()
                        .anyMatch(cs -> cs.getSkill().getId().equals(skill.getId()));
                if (!exists) {
                    cp.getSkills().add(CandidateSkill.builder()
                            .candidate(cp).skill(skill)
                            .proficiency("INTERMEDIATE").aiExtracted(true).build());
                }
            });
        }
        candidateRepo.save(cp);
    }
}