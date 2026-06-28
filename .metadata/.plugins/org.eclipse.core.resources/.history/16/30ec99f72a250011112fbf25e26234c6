package com.superflick.modules.file;

import com.superflick.modules.candidate.entity.CandidateProfile;
import com.superflick.modules.candidate.repository.CandidateRepository;
import com.superflick.modules.user.entity.User;
import com.superflick.shared.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

@RestController
@RequestMapping("/api/v1/files")
@RequiredArgsConstructor
@PreAuthorize("isAuthenticated()")
public class FileController {

    private final FileService fileService;
    private final CandidateRepository candidateRepo;

    @PostMapping("/resume")
    public ResponseEntity<String> uploadResume(@AuthenticationPrincipal User user,
                                               @RequestParam MultipartFile file) {
        // Validate file type
        String ct = file.getContentType();
        if (ct == null || (!ct.equals("application/pdf") && !ct.contains("msword"))) {
            throw new BadRequestException("Only PDF or DOC files allowed");
        }
        String url = fileService.uploadFile(file, "resumes/");
        CandidateProfile cp = candidateRepo.findByUserId(user.getId()).orElseThrow();
        cp.setResumeUrl(url);
        candidateRepo.save(cp);
        return ResponseEntity.ok(url);
    }
}