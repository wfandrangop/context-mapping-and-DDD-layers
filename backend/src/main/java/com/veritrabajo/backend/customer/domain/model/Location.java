package com.veritrabajo.backend.customer.domain.model;

import java.util.Objects;

/**
 * Value object that represents a geographical point with a textual reference.
 * Vital for the geolocation engine that pairs customers with nearby workers.
 */
public final class Location {

    private static final double LATITUDE_MIN = -90.0;
    private static final double LATITUDE_MAX = 90.0;
    private static final double LONGITUDE_MIN = -180.0;
    private static final double LONGITUDE_MAX = 180.0;

    private final double latitude;
    private final double longitude;
    private final String description;

    private Location(double latitude, double longitude, String description) {
        this.latitude = latitude;
        this.longitude = longitude;
        this.description = description;
    }

    public static Location of(double latitude, double longitude, String description) {
        validateLatitude(latitude);
        validateLongitude(longitude);
        return new Location(latitude, longitude, validateDescription(description));
    }

    public double latitude() {
        return latitude;
    }

    public double longitude() {
        return longitude;
    }

    public String description() {
        return description;
    }

    private static void validateLatitude(double latitude) {
        if (latitude < LATITUDE_MIN || latitude > LATITUDE_MAX) {
            throw new IllegalArgumentException(
                    "Latitude must be between -90 and 90, got " + latitude);
        }
    }

    private static void validateLongitude(double longitude) {
        if (longitude < LONGITUDE_MIN || longitude > LONGITUDE_MAX) {
            throw new IllegalArgumentException(
                    "Longitude must be between -180 and 180, got " + longitude);
        }
    }

    private static String validateDescription(String description) {
        if (description == null || description.isBlank()) {
            throw new IllegalArgumentException("Location description is required");
        }
        return description.trim();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Location that)) {
            return false;
        }
        return Double.compare(latitude, that.latitude) == 0
                && Double.compare(longitude, that.longitude) == 0
                && description.equals(that.description);
    }

    @Override
    public int hashCode() {
        return Objects.hash(latitude, longitude, description);
    }
}
