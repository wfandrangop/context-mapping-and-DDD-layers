package com.veritrabajo.backend.workerprofile.domain.model;

public record Availability(String cityOrZone) {

    public Availability {
        if (cityOrZone == null || cityOrZone.isBlank()) {
            throw new IllegalArgumentException(
                    "City or zone cannot be empty."
            );
        }
        cityOrZone = cityOrZone.trim();
    }
}
