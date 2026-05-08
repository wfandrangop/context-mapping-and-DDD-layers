package com.veritrabajo.backend.identityaccess.application;

import com.veritrabajo.backend.identityaccess.application.dto.AuthResponse;
import com.veritrabajo.backend.identityaccess.application.dto.AuthUserView;
import com.veritrabajo.backend.identityaccess.application.dto.LoginRequest;
import com.veritrabajo.backend.identityaccess.application.dto.RegisterAuthRequest;
import com.veritrabajo.backend.identityaccess.domain.model.AuthUser;
import com.veritrabajo.backend.identityaccess.domain.model.Role;
import com.veritrabajo.backend.identityaccess.domain.repository.AuthUserRepository;
import com.veritrabajo.backend.identityaccess.infrastructure.security.JwtProvider;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AuthService {

    private final AuthUserRepository authUserRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtProvider jwtProvider;

    public AuthService(
            AuthUserRepository authUserRepository,
            PasswordEncoder passwordEncoder,
            JwtProvider jwtProvider
    ) {
        this.authUserRepository = authUserRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtProvider = jwtProvider;
    }

    public AuthResponse register(RegisterAuthRequest request) {
        String email = normalizedEmail(request.getEmail());
        if (authUserRepository.existsByEmail(email)) {
            throw new IllegalStateException("Email is already registered");
        }

        Role role = parseRole(request.getRole());
        String passwordHash = passwordEncoder.encode(requirePassword(request.getPassword()));
        AuthUser created = authUserRepository.save(AuthUser.register(email, passwordHash, role));

        List<String> roles = created.getRoles().stream().map(Role::name).toList();
        String token = jwtProvider.generateToken(created.getId(), created.getEmail(), roles);
        return AuthResponse.from(toView(created, roles), token);
    }

    public AuthResponse login(LoginRequest request) {
        String email = normalizedEmail(request.getEmail());
        String password = requirePassword(request.getPassword());
        AuthUser user = authUserRepository.findByEmail(email);
        if (user == null || !passwordEncoder.matches(password, user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid email or password");
        }

        List<String> roles = user.getRoles().stream().map(Role::name).toList();
        String token = jwtProvider.generateToken(user.getId(), user.getEmail(), roles);
        return AuthResponse.from(toView(user, roles), token);
    }

    private AuthUserView toView(
            AuthUser user,
            List<String> roles
    ) {
        return new AuthUserView(user.getId(), user.getEmail(), roles);
    }

    private String normalizedEmail(String email) {
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email is required");
        }
        return email.trim().toLowerCase();
    }

    private String requirePassword(String password) {
        if (password == null || password.isBlank()) {
            throw new IllegalArgumentException("Password is required");
        }
        return password;
    }

    private Role parseRole(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("Role is required");
        }
        try {
            return Role.valueOf(value.trim().toUpperCase());
        } catch (IllegalArgumentException exception) {
            throw new IllegalArgumentException(
                    "Role must be CUSTOMER or WORKER"
            );
        }
    }
}
