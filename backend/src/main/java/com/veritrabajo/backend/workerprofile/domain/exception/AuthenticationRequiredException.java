package com.veritrabajo.backend.workerprofile.domain.exception;

/**
 * Raised by the ACL adapter when no authenticated principal is available for the current
 * request. Maps to {@code 401} via the shared API exception handler.
 */
public class AuthenticationRequiredException extends RuntimeException {

    public AuthenticationRequiredException() {
        super("Authentication required");
    }
}
