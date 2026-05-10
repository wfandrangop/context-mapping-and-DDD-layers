package com.veritrabajo.backend.customer.infrastructure.acl;

import com.veritrabajo.backend.customer.domain.exception.AuthenticationRequiredException;
import com.veritrabajo.backend.customer.domain.model.AuthUserId;
import com.veritrabajo.backend.customer.domain.port.AuthenticatedIdentityProvider;
import com.veritrabajo.backend.identityaccess.infrastructure.security.AuthenticatedUser;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

/**
 * Anti-Corruption Layer adapter that translates the OHS contract published by the
 * IdentityAccess context (Spring Security {@link Authentication} + {@link AuthenticatedUser}
 * principal) into the local {@link AuthUserId} value object.
 *
 * <p>This is the ONLY class in the Customer module that imports
 * {@code org.springframework.security.*} or types from {@code identityaccess.*}. Keeping
 * the dependency confined here is what preserves the bounded-context boundary.
 */
@Component("customerJwtIdentityAdapter")
public class JwtAuthenticatedIdentityAdapter implements AuthenticatedIdentityProvider {

    @Override
    public AuthUserId currentAuthUserId() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null
                || !(authentication.getPrincipal() instanceof AuthenticatedUser principal)) {
            throw new AuthenticationRequiredException();
        }
        return AuthUserId.of(principal.userId());
    }
}
