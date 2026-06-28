package com.superflick.modules.auth.oauth;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class MicrosoftOAuthProvider implements OAuthProvider {

    @Override
    public boolean supports(String providerName) {
        return "MICROSOFT".equalsIgnoreCase(providerName);
    }

    @Override
    public OAuthUserInfo fetchUserInfo(String authorizationCode) {
        log.info("Microsoft OAuth exchange for code={}", authorizationCode);
        throw new UnsupportedOperationException("Microsoft OAuth not yet configured.");
    }
}