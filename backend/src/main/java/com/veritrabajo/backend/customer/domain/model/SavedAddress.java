package com.veritrabajo.backend.customer.domain.model;

import java.util.Objects;
import java.util.UUID;

/**
 * Entity representing one of the customer's saved addresses (e.g., Home, Office).
 * Has its own identity within the Customer aggregate so that individual
 * editing is allowed.
 */
public final class SavedAddress {

    private final UUID id;
    private final String label;
    private final Location location;

    private SavedAddress(UUID id, String label, Location location) {
        this.id = Objects.requireNonNull(id, "Address id is required");
        this.label = validateLabel(label);
        this.location = Objects.requireNonNull(location, "Location is required");
    }

    public static SavedAddress create(String label, Location location) {
        return new SavedAddress(UUID.randomUUID(), label, location);
    }

    public static SavedAddress rehydrate(UUID id, String label, Location location) {
        return new SavedAddress(id, label, location);
    }

    public UUID id() {
        return id;
    }

    public String label() {
        return label;
    }

    public Location location() {
        return location;
    }

    private static String validateLabel(String label) {
        if (label == null || label.isBlank()) {
            throw new IllegalArgumentException("Address label is required");
        }
        return label.trim();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof SavedAddress that)) {
            return false;
        }
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
