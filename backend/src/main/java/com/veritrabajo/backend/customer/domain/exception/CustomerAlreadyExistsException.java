package com.veritrabajo.backend.customer.domain.exception;

import com.veritrabajo.backend.customer.domain.model.AuthUserId;

/**
 * Raised when a customer profile already exists for the given authenticated user.
 * Extends {@link IllegalStateException} so it is mapped to {@code 409 Conflict} by the
 * shared API exception handler.
 */
public class CustomerAlreadyExistsException extends IllegalStateException {

    public CustomerAlreadyExistsException(AuthUserId authUserId) {
        super("Customer profile already exists for authUserId=" + authUserId.value());
    }
}
