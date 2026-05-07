package com.veritrabajo.backend.customer.domain.exception;

import java.util.UUID;

/**
 * Exception thrown when a customer cannot be found.
 */
public class CustomerNotFoundException extends RuntimeException {

    public CustomerNotFoundException(UUID customerId) {
        super("Customer not found: " + customerId);
    }

    public CustomerNotFoundException(String email) {
        super("Customer not found by email: " + email);
    }
}
