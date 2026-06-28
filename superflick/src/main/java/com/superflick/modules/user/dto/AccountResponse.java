package com.superflick.modules.user.dto;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;
import java.time.LocalDateTime;
import java.util.UUID;

/** Used by Super Admin account management table. */
@Data @Builder @NoArgsConstructor @AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class AccountResponse {
    private UUID id;
    private String email;
    private String phone;
    /** CANDIDATE | HR | ADMIN | SUPER_ADMIN */
    private String role;
    /** ACTIVE | BLOCKED */
    private String status;
    /** Company name for HR accounts. Null for candidates/admins. */
    private String company;
    private boolean profileComplete;
    private String authProvider;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private LocalDateTime createdAt;
}