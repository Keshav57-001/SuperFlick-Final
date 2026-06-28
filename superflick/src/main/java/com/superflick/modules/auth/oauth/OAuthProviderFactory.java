package com.superflick.modules.auth.oauth;

import com.superflick.shared.exception.BadRequestException;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;
import java.util.List;

/**
 * Selects the correct OAuthProvider implementation based on the provider name.
 * New providers are registered simply by adding a new @Component that
 * implements OAuthProvider — no changes needed here.
 */
@Component
@RequiredArgsConstructor
public class OAuthProviderFactory {

    private final List<OAuthProvider> providers;

    /**
     * Returns the OAuthProvider for the given provider name (case-insensitive).
     *
     * @param providerName GOOGLE | MICROSOFT | GITHUB | LINKEDIN
     * @return matching OAuthProvider implementation
     * @throws BadRequestException if no provider matches
     */
    public OAuthProvider getProvider(String providerName) {
        return providers.stream()
                .filter(p -> p.supports(providerName))
                .findFirst()
                .orElseThrow(() -> new BadRequestException(
                        "Unsupported OAuth provider: " + providerName +
                                ". Allowed: GOOGLE, MICROSOFT, GITHUB, LINKEDIN"));
    }
}