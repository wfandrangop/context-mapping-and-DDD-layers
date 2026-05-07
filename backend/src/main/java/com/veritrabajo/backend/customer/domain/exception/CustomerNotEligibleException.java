package com.veritrabajo.backend.customer.domain.exception;

import java.util.UUID;

/**
 * Exception thrown when a customer fails the eligibility rules
 * to request a new service.
 */
public class CustomerNotEligibleException extends RuntimeException {

    public CustomerNotEligibleException(UUID customerId, String reason) {
        super("Customer " + customerId + " is not eligible to request a service: " + reason);
    }
}
