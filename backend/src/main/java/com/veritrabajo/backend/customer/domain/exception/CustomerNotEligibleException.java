package com.veritrabajo.backend.customer.domain.exception;

import java.util.UUID;

public class CustomerNotEligibleException extends RuntimeException {

    public CustomerNotEligibleException(UUID customerId, String reason) {
        super("Customer " + customerId + " is not eligible to request a service: " + reason);
    }
}
