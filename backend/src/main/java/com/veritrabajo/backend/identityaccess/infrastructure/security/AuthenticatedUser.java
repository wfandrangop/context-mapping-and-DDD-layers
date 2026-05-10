package com.veritrabajo.backend.identityaccess.infrastructure.security;

import java.util.List;

/**
 * Published Open Host Service (OHS) contract for authenticated identity.
 *
 * <p>This record is the principal payload that the IdentityAccess context exposes to other
 * bounded contexts via Spring Security. Downstream modules (WorkerProfile, Customer, ...)
 * MUST translate it through their own Anti-Corruption Layer adapters located in
 * {@code <module>/infrastructure/acl/}; they MUST NOT import this type anywhere else in
 * their package tree. Keeping the dependency confined to a single ACL class is what
 * preserves the bounded-context boundary.
 *
 * <p>Field semantics:
 * <ul>
 *     <li>{@code userId} — the canonical user identifier (mirrors the {@code uid} JWT claim)</li>
 *     <li>{@code email} — JWT subject; informational</li>
 *     <li>{@code roles} — authorization roles (e.g. {@code WORKER}, {@code CUSTOMER})</li>
 * </ul>
 */
public record AuthenticatedUser(
        String userId,
        String email,
        List<String> roles
) {
}
