package com.veritrabajo.backend.customer.domain.model;

import java.util.Objects;

public record CustomerCreation(
        String name,
        ContactInfo contactInfo,
        ClientPreferences preferences
) {
    public CustomerCreation {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Customer name is required");
        }
        Objects.requireNonNull(contactInfo, "Contact info is required");
        Objects.requireNonNull(preferences, "Preferences are required");
    }
}
