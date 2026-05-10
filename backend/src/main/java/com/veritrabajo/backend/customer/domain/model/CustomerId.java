package com.veritrabajo.backend.customer.domain.model;

import java.util.Objects;
import java.util.UUID;

public record CustomerId(UUID value) {

    public CustomerId {
        Objects.requireNonNull(value, "CustomerId value cannot be null");
    }

    public static CustomerId generate() {
        return new CustomerId(UUID.randomUUID());
    }

    public static CustomerId of(UUID value) {
        return new CustomerId(value);
    }

    public static CustomerId fromString(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("CustomerId string cannot be blank");
        }
        return new CustomerId(UUID.fromString(value));
    }
}
