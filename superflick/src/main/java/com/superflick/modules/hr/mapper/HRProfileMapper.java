package com.superflick.modules.hr.mapper;

import com.superflick.modules.hr.dto.HRProfileRequest;
import com.superflick.modules.hr.dto.HRProfileResponse;
import com.superflick.modules.hr.entity.HRProfile;
import com.superflick.modules.user.entity.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@RequiredArgsConstructor
public class HRProfileMapper {

    private final CompanyMapper companyMapper;

    public HRProfileResponse toResponse(HRProfile hr) {
        if (hr == null) return null;

        User user = hr.getUser();
        boolean isPremium = hr.getPremiumUntil() != null
                && hr.getPremiumUntil().isAfter(LocalDateTime.now());

        return HRProfileResponse.builder()
                .id(hr.getId())
                .userId(user != null ? user.getId() : null)
                .fullName(hr.getFullName())
                .email(user != null ? user.getEmail() : null)
                .phone(user != null ? user.getPhone() : null)
                .designation(hr.getDesignation())
                .verified(hr.isVerified())
                .dailySwipesUsed(hr.getDailySwipesUsed())
                .premiumUntil(hr.getPremiumUntil())
                .premium(isPremium)
                .hiringDepartment(hr.getHiringDepartment())
                .hiringRoles(hr.getHiringRoles())
                .expectedHiringVolume(hr.getExpectedHiringVolume())
                .company(companyMapper.toResponse(hr.getCompany()))
                .build();
    }

    public void updateEntity(HRProfile hr, HRProfileRequest req) {
        hr.setFullName(req.getFullName());
        hr.setDesignation(req.getDesignation());
        hr.setHiringDepartment(req.getHiringDepartment());
        hr.setHiringRoles(req.getHiringRoles());
        hr.setExpectedHiringVolume(req.getExpectedHiringVolume());
    }
}