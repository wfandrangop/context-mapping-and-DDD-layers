package com.veritrabajo.backend.identityaccess.infrastructure.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.nio.charset.StandardCharsets;
import java.time.Instant;
import java.util.Date;
import java.util.List;

/**
 * Issues and parses the JWT that constitutes the IdentityAccess Open Host Service contract.
 *
 * <p>Token shape (consumed by the JWT filter and, transitively, by ACL adapters in other
 * contexts): {@code sub = email}, custom claim {@code uid = AuthUser.id} (UUID string),
 * custom claim {@code roles = ["WORKER"|"CUSTOMER"|...]}, signed with HS256.
 *
 * <p>Downstream modules must not depend on this class — they consume the contract via
 * {@link AuthenticatedUser} after Spring Security has populated the {@code SecurityContext}.
 */
@Component
public class JwtProvider {

    private final SecretKey signingKey;
    private final long expirationMillis;

    public JwtProvider(
            @Value("${security.jwt.secret:change-this-secret-key-at-least-32-bytes}") String secret,
            @Value("${security.jwt.expiration-ms:86400000}") long expirationMillis
    ) {
        this.signingKey = Keys.hmacShaKeyFor(secret.getBytes(StandardCharsets.UTF_8));
        this.expirationMillis = expirationMillis;
    }

    public String generateToken(
            String userId,
            String email,
            List<String> roles
    ) {
        Instant now = Instant.now();
        Instant expiresAt = now.plusMillis(expirationMillis);
        return Jwts.builder()
                .setSubject(email)
                .claim("uid", userId)
                .claim("roles", roles)
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiresAt))
                .signWith(signingKey, SignatureAlgorithm.HS256)
                .compact();
    }

    public AuthenticatedUser parse(String token) {
        Claims claims = Jwts.parserBuilder()
                .setSigningKey(signingKey)
                .build()
                .parseClaimsJws(token)
                .getBody();

        String userId = claims.get("uid", String.class);
        String email = claims.getSubject();
        @SuppressWarnings("unchecked")
        List<String> roles = claims.get("roles", List.class);
        return new AuthenticatedUser(userId, email, roles);
    }
}
