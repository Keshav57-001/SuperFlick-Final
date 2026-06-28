package com.superflick.modules.auth.oauth;

import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
public class GoogleOAuthProvider implements OAuthProvider {

    @Override
    public boolean supports(String providerName) {
        return "GOOGLE".equalsIgnoreCase(providerName);
    }

    @Override
    public OAuthUserInfo fetchUserInfo(String authorizationCode) {
        // TODO: Exchange code for access token via Google OAuth2 token endpoint,
        //       then call https://www.googleapis.com/oauth2/v3/userinfo
        log.info("Google OAuth exchange for code={}", authorizationCode);
        throw new UnsupportedOperationException(
                "Google OAuth integration not yet configured. " +
                        "Set GOOGLE_CLIENT_ID and GOOGLE_CLIENT_SECRET and implement this method.");
    }
}