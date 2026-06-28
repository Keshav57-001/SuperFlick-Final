package com.superflick.modules.auth.oauth;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class GitHubOAuthProvider implements OAuthProvider {

    @Override
    public boolean supports(String providerName) {
        return "GITHUB".equalsIgnoreCase(providerName);
    }

    @Override
    public OAuthUserInfo fetchUserInfo(String authorizationCode) {
        log.info("GitHub OAuth exchange for code={}", authorizationCode);
        throw new UnsupportedOperationException("GitHub OAuth not yet configured.");
    }
}