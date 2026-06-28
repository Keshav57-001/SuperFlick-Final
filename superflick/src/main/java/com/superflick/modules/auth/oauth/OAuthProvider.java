package com.superflick.modules.auth.oauth;

/**
 * Strategy interface for OAuth provider integrations.
 * Each provider (Google, Microsoft, GitHub, LinkedIn) implements this.
 */
public interface OAuthProvider {
    /** Returns true if this provider handles the given provider name. */
    boolean supports(String providerName);
    /** Exchanges an authorization code for user info from the OAuth provider. */
    OAuthUserInfo fetchUserInfo(String authorizationCode);
}