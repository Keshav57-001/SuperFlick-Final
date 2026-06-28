package com.superflick.modules.auth.dto;

import com.superflick.shared.enums.UserRole;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class AuthResponse {
    private String   token;
    private UserRole role;
    /** true = send user to /setup/candidate or /setup/hr */
    private boolean  needsProfileSetup;
}