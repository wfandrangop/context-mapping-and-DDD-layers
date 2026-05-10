package com.veritrabajo.backend.customer.domain.exception;

/**
 * Raised by the ACL adapter when no authenticated principal is available for the current
 * request.
 */
public class AuthenticationRequiredException extends RuntimeException {

    public AuthenticationRequiredException() {
        super("Authentication required");
    }
}
