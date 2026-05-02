package com.veritrabajo.backend.reputation.event;

/**
 * Domain event published when a professional profile is created.
 */
public record ProfessionalProfileCreated(String profileId) {
    public ProfessionalProfileCreated {
        if (profileId == null || profileId.isBlank()) {
            throw new IllegalArgumentException("Profile id is required");
        }
    }
}
