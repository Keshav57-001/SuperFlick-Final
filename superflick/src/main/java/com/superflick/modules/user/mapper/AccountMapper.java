package com.superflick.modules.user.mapper;

import com.superflick.modules.user.dto.AccountResponse;
import com.superflick.modules.user.entity.User;
import org.springframework.stereotype.Component;

@Component
public class AccountMapper {
    /**
     * Maps User to AccountResponse for the Super Admin account table.
     * Resolves the company name from the HR profile if applicable.
     */
    public AccountResponse toResponse(User user) {
        if (user == null) return null;
        String company = null;
        if (user.getHrProfile() != null && user.getHrProfile().getCompany() != null) {
            company = user.getHrProfile().getCompany().getName();
        }
        return AccountResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole() != null ? user.getRole().name() : null)
                .status(user.getStatus() != null ? user.getStatus().name() : null)
                .company(company)
                .profileComplete(user.isProfileComplete())
                .authProvider(user.getAuthProvider() != null ? user.getAuthProvider().name() : null)
                .createdAt(user.getCreatedAt())
                .build();
    }
}