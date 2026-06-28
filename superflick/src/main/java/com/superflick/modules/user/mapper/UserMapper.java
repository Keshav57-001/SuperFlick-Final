package com.superflick.modules.user.mapper;

import com.superflick.modules.user.dto.UserResponse;
import com.superflick.modules.user.entity.User;
import org.springframework.stereotype.Component;

@Component
public class UserMapper {
    /**
     * Maps User entity to UserResponse DTO.
     * Used for GET /users/me and auth response population.
     */
    public UserResponse toResponse(User user) {
        if (user == null) return null;
        return UserResponse.builder()
                .id(user.getId())
                .email(user.getEmail())
                .phone(user.getPhone())
                .role(user.getRole() != null ? user.getRole().name() : null)
                .status(user.getStatus() != null ? user.getStatus().name() : null)
                .authProvider(user.getAuthProvider() != null ? user.getAuthProvider().name() : null)
                .profileComplete(user.isProfileComplete())
                .createdAt(user.getCreatedAt())
                .build();
    }
}