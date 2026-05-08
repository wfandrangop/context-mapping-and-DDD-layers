package com.veritrabajo.backend.identityaccess.infrastructure.security;

import java.util.List;

public record AuthenticatedUser(
        String userId,
        String email,
        List<String> roles
) {
}
