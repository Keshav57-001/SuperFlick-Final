package com.superflick.modules.auth;

import com.superflick.modules.user.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Spring Security UserDetailsService implementation.
 *
 * The JWT token stores the user's UUID as the subject (not email).
 * JwtAuthFilter extracts the UUID string from the token and calls
 * loadUserByUsername(uuidString) here.
 *
 * Our User entity implements UserDetails, so we return it directly —
 * no wrapping needed.
 */
@Service("customUserDetailsService")
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserRepository userRepo;

    @Override
    public UserDetails loadUserByUsername(String userId) throws UsernameNotFoundException {
        try {
            UUID uuid = UUID.fromString(userId);
            return userRepo.findById(uuid)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found: " + userId));
        } catch (IllegalArgumentException ex) {
            // userId might be an email (e.g. during form-login fallback)
            return userRepo.findByEmail(userId)
                    .orElseThrow(() -> new UsernameNotFoundException("User not found: " + userId));
        }
    }
}