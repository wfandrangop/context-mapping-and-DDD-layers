package com.veritrabajo.backend.reputation.application.integration;

/**
 * Integration event contract representing a professional profile creation
 * from an upstream bounded context (e.g., ProfessionalProfile).
 * <p>
 * Reputation is the downstream consumer of this event.
 * This contract is owned by the reputation context as part of its
 * anti-corruption layer, defining the shape of data it expects.
 */
public record ProfessionalProfileCreated(String profileId) {
    public ProfessionalProfileCreated {
        if (profileId == null || profileId.isBlank()) {
            throw new IllegalArgumentException("Profile id is required");
        }
    }
}
