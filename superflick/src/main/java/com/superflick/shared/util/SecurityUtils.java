package com.superflick.shared.util;

import com.superflick.modules.user.entity.User;
import com.superflick.shared.enums.UserRole;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;

import java.util.Optional;
import java.util.UUID;

/**
 * Static helpers for accessing the current authenticated user
 * from the Spring Security context.
 *
 * These methods are safe to call from any layer (service, filter, etc.)
 * without injecting a dependency.
 */
public final class SecurityUtils {

    private SecurityUtils() {}

    /**
     * Returns the currently authenticated User entity.
     *
     * @return Optional<User> — empty if no authentication is present
     *         (e.g. public endpoints, or called from an async thread)
     */
    public static Optional<User> getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth == null || !auth.isAuthenticated()) return Optional.empty();
        Object principal = auth.getPrincipal();
        if (principal instanceof User user) return Optional.of(user);
        return Optional.empty();
    }

    /**
     * Returns the currently authenticated user's UUID.
     *
     * @throws IllegalStateException if there is no authenticated user
     */
    public static UUID getCurrentUserId() {
        return getCurrentUser()
                .map(User::getId)
                .orElseThrow(() -> new IllegalStateException("No authenticated user in security context"));
    }

    /**
     * Returns the currently authenticated user's role.
     */
    public static Optional<UserRole> getCurrentUserRole() {
        return getCurrentUser().map(User::getRole);
    }

    /**
     * Checks whether the current user has the given role.
     */
    public static boolean hasRole(UserRole role) {
        return getCurrentUserRole().map(r -> r == role).orElse(false);
    }

    /**
     * Returns true if the current user is ADMIN or SUPER_ADMIN.
     */
    public static boolean isAdmin() {
        return getCurrentUserRole()
                .map(r -> r == UserRole.ADMIN || r == UserRole.SUPER_ADMIN)
                .orElse(false);
    }

    /**
     * Returns true if the given userId matches the current authenticated user.
     * Used in service methods to allow users to access only their own data.
     */
    public static boolean isCurrentUser(UUID userId) {
        return getCurrentUser().map(u -> u.getId().equals(userId)).orElse(false);
    }
}