package com.veritrabajo.backend.customer.domain.event;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public record CustomerRegistered(UUID customerId, String email, Instant occurredAt) {

    public CustomerRegistered(UUID customerId, String email) {
        this(customerId, email, Instant.now());
    }

    public CustomerRegistered {
        Objects.requireNonNull(customerId, "Customer id is required");
        if (email == null || email.isBlank()) {
            throw new IllegalArgumentException("Email is required");
        }
        Objects.requireNonNull(occurredAt, "Occurred-at timestamp is required");
    }
}
