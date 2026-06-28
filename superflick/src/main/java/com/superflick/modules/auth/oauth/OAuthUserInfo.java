package com.superflick.modules.auth.oauth;

import lombok.*;

@Data @Builder @NoArgsConstructor @AllArgsConstructor
public class OAuthUserInfo {
    private String email;
    private String name;
    private String profilePictureUrl;
    /** Role passed from the frontend callback request (CANDIDATE or HR). */
    private String role;
}