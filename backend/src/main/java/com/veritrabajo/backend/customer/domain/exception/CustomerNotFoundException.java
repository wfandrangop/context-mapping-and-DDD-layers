package com.veritrabajo.backend.customer.domain.exception;

import java.util.UUID;

public class CustomerNotFoundException extends RuntimeException {

    public CustomerNotFoundException(UUID customerId) {
        super("Customer not found: " + customerId);
    }

    public CustomerNotFoundException(String email) {
        super("Customer not found by email: " + email);
    }
}
