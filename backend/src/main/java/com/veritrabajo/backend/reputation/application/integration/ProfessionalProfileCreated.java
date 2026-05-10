package com.veritrabajo.backend.reputation.application.integration;

public record ProfessionalProfileCreated(String profileId) {
    public ProfessionalProfileCreated {
        if (profileId == null || profileId.isBlank()) {
            throw new IllegalArgumentException("Profile id is required");
        }
    }
}
