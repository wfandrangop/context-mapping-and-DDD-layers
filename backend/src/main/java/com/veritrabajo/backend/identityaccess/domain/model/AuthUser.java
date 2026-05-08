package com.veritrabajo.backend.identityaccess.domain.model;

import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

/**
 * Identity aggregate root for authentication/authorization concerns.
 */
public final class AuthUser {

    private final String id;
    private final String email;
    private final String passwordHash;
    private final Set<Role> roles;

    private AuthUser(
            String id,
            String email,
            String passwordHash
    ) {
        this.id = id;
        this.email = email;
        this.passwordHash = passwordHash;
        this.roles = new HashSet<>();
    }

    public static AuthUser register(
            String email,
            String passwordHash,
            Role role
    ) {
        requireNonBlank(email, "Email cannot be blank");
        requireNonBlank(passwordHash, "Password hash cannot be blank");
        if (role == null) {
            throw new IllegalArgumentException("Role is required");
        }
        AuthUser user = new AuthUser(
                UUID.randomUUID().toString(),
                email.trim().toLowerCase(),
                passwordHash
        );
        user.roles.add(role);
        return user;
    }

    public static AuthUser restore(RestoredAuthUser data) {
        requireNonBlank(data.id(), "User id cannot be blank");
        requireNonBlank(data.email(), "Email cannot be blank");
        requireNonBlank(data.passwordHash(), "Password hash cannot be blank");
        Set<Role> roles = data.roles();
        if (roles == null || roles.isEmpty()) {
            throw new IllegalArgumentException("Roles cannot be empty");
        }
        AuthUser user = new AuthUser(
                data.id().trim(),
                data.email().trim().toLowerCase(),
                data.passwordHash()
        );
        user.roles.addAll(roles);
        return user;
    }

    public String getId() {
        return id;
    }

    public String getEmail() {
        return email;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public Set<Role> getRoles() {
        return Collections.unmodifiableSet(roles);
    }

    private static void requireNonBlank(
            String value,
            String message
    ) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(message);
        }
    }

    public record RestoredAuthUser(
            String id,
            String email,
            String passwordHash,
            Set<Role> roles
    ) {
    }
}
