package com.veritrabajo.backend.identityaccess.infrastructure.persistence;

import com.veritrabajo.backend.identityaccess.domain.model.AuthUser;
import com.veritrabajo.backend.identityaccess.domain.repository.AuthUserRepository;
import org.springframework.stereotype.Repository;

@Repository
public class JpaAuthUserRepository implements AuthUserRepository {

    private final SpringDataAuthUserRepository springRepository;

    public JpaAuthUserRepository(SpringDataAuthUserRepository springRepository) {
        this.springRepository = springRepository;
    }

    @Override
    public AuthUser save(AuthUser user) {
        AuthUserEntity entity = new AuthUserEntity();
        entity.setId(user.getId());
        entity.setEmail(user.getEmail());
        entity.setPasswordHash(user.getPasswordHash());
        entity.setRoles(user.getRoles());
        AuthUserEntity saved = springRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public AuthUser findByEmail(String email) {
        return springRepository.findByEmail(email)
                .map(this::toDomain)
                .orElse(null);
    }

    @Override
    public boolean existsByEmail(String email) {
        return springRepository.existsByEmail(email);
    }

    private AuthUser toDomain(AuthUserEntity entity) {
        return AuthUser.restore(new AuthUser.RestoredAuthUser(
                entity.getId(),
                entity.getEmail(),
                entity.getPasswordHash(),
                entity.getRoles()
        ));
    }
}
