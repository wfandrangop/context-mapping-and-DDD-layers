package com.veritrabajo.backend.customer.domain.model;

/**
 * Reference to the authenticated user (issued by the IdentityAccess context) as understood
 * locally by the Customer context. This is a downstream-owned value object — it is
 * intentionally not imported from IdentityAccess so the bounded contexts stay decoupled.
 *
 * <p>The {@code value} mirrors the {@code uid} claim of the OHS-published JWT.
 */
public record AuthUserId(String value) {

    public AuthUserId {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("AuthUserId value cannot be blank");
        }
    }

    public static AuthUserId of(String value) {
        return new AuthUserId(value);
    }
}
