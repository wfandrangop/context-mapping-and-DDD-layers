package com.veritrabajo.backend.workerprofile.domain.model;

import java.util.Objects;
import java.util.UUID;

public record WorkerId(UUID value) {

    public WorkerId {
        Objects.requireNonNull(value, "WorkerId value cannot be null");
    }

    public static WorkerId generate() {
        return new WorkerId(UUID.randomUUID());
    }

    public static WorkerId of(UUID value) {
        return new WorkerId(value);
    }

    public static WorkerId fromString(String value) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException("WorkerId string cannot be blank");
        }
        return new WorkerId(UUID.fromString(value));
    }

    public String asString() {
        return value.toString();
    }
}
