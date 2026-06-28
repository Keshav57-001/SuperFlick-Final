package com.superflick.modules.hr;

import com.superflick.modules.file.FileService;                  // ← superflick FileService, NOT stripe
import com.superflick.modules.hr.dto.HRProfileRequest;
import com.superflick.modules.hr.dto.HRProfileResponse;
import com.superflick.modules.hr.entity.Company;
import com.superflick.modules.hr.entity.HRProfile;
import com.superflick.modules.hr.mapper.CompanyMapper;
import com.superflick.modules.hr.mapper.HRProfileMapper;
import com.superflick.modules.hr.repository.CompanyRepository;
import com.superflick.modules.hr.repository.HRRepository;
import com.superflick.modules.user.entity.User;
import com.superflick.modules.user.repository.UserRepository;
import com.superflick.shared.exception.ConflictException;
import com.superflick.shared.exception.NotFoundException;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Slf4j
@Service
@RequiredArgsConstructor
@Transactional
public class HRService {

    private final HRRepository      hrRepo;
    private final CompanyRepository companyRepo;
    private final UserRepository    userRepo;
    private final FileService       fileService;     // com.superflick.modules.file.FileService ✓
    private final HRProfileMapper   hrMapper;        // injected instance ✓
    private final CompanyMapper     companyMapper;   // injected instance ✓

    public HRProfileResponse createProfile(User user, HRProfileRequest req) {
        if (hrRepo.existsByUserId(user.getId()))
            throw new ConflictException("HR profile already exists");

        // Company data comes via req.getCompany() — a nested CompanyRequest object
        Company company = companyMapper.toEntity(req.getCompany());
        companyRepo.save(company);

        HRProfile hr = HRProfile.builder()
                .user(user)
                .fullName(req.getFullName())
                .designation(req.getDesignation())
                .company(company)
                .hiringDepartment(req.getHiringDepartment())
                .hiringRoles(req.getHiringRoles())
                .expectedHiringVolume(req.getExpectedHiringVolume())
                .build();
        hrRepo.save(hr);

        user.setProfileComplete(true);
        userRepo.save(user);

        log.info("HR profile created for userId={}", user.getId());
        return hrMapper.toResponse(hr);              // instance call ✓
    }

    public HRProfileResponse getProfile(UUID userId) {
        HRProfile hr = hrRepo.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("HR profile not found"));
        return hrMapper.toResponse(hr);              // instance call ✓
    }

    public HRProfileResponse updateProfile(UUID userId, HRProfileRequest req) {
        HRProfile hr = hrRepo.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("HR profile not found"));

        hrMapper.updateEntity(hr, req);              // instance call ✓

        if (hr.getCompany() != null && req.getCompany() != null) {
            companyMapper.updateEntity(hr.getCompany(), req.getCompany());
            companyRepo.save(hr.getCompany());
        }

        HRProfile saved = hrRepo.save(hr);
        return hrMapper.toResponse(saved);           // instance call ✓
    }

    public String uploadCompanyLogo(UUID userId, MultipartFile file) {
        HRProfile hr = hrRepo.findByUserId(userId)
                .orElseThrow(() -> new NotFoundException("HR profile not found"));
        String url = fileService.uploadFile(file, "logos/");  // superflick FileService.uploadFile() ✓
        hr.getCompany().setLogoUrl(url);
        companyRepo.save(hr.getCompany());
        return url;
    }
}