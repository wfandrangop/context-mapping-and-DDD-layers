package com.veritrabajo.backend.identityaccess.domain.repository;

import com.veritrabajo.backend.identityaccess.domain.model.AuthUser;

public interface AuthUserRepository {

    AuthUser save(AuthUser user);

    AuthUser findByEmail(String email);

    boolean existsByEmail(String email);
}
