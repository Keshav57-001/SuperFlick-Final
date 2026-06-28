package com.superflick.modules.auth.oauth;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class LinkedInOAuthProvider implements OAuthProvider {

    @Override
    public boolean supports(String providerName) {
        return "LINKEDIN".equalsIgnoreCase(providerName);
    }

    @Override
    public OAuthUserInfo fetchUserInfo(String authorizationCode) {
        log.info("LinkedIn OAuth exchange for code={}", authorizationCode);
        throw new UnsupportedOperationException("LinkedIn OAuth not yet configured.");
    }
}