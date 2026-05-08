package com.veritrabajo.backend.identityaccess.application.dto;

import java.util.List;

public record AuthUserView(
        String userId,
        String email,
        List<String> roles
) {
}
