package com.veritrabajo.backend.identityaccess.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpringDataAuthUserRepository
        extends JpaRepository<AuthUserEntity, String> {

    Optional<AuthUserEntity> findByEmail(String email);

    boolean existsByEmail(String email);
}
