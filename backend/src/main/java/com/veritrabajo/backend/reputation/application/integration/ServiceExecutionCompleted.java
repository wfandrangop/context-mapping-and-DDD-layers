package com.veritrabajo.backend.reputation.application.integration;

import java.util.Objects;
import java.util.UUID;

/**
 * Integration event contract representing a completed service execution
 * from the upstream ServiceExecution bounded context.
 * <p>
 * Relationship: ServiceExecution (Upstream) → Reputation (Downstream) — Partnership.
 * <p>
 * Reputation defines this contract as part of its anti-corruption layer,
 * specifying exactly the data it needs to update reputation metrics.
 */
public record ServiceExecutionCompleted(
    UUID serviceId,
    String profileId,
    int clientRating,
    String clientComment,
    boolean completedSuccessfully
) {
    private static final int MIN_RATING = 1;
    private static final int MAX_RATING = 5;

    public ServiceExecutionCompleted {
        Objects.requireNonNull(serviceId, "Service id is required");
        validateProfileId(profileId);
        validateRating(clientRating);
        validateComment(clientComment);
    }

    private static void validateProfileId(String profileId) {
        if (profileId == null || profileId.isBlank()) {
            throw new IllegalArgumentException("Profile id is required");
        }
    }

    private static void validateRating(int rating) {
        if (rating < MIN_RATING || rating > MAX_RATING) {
            throw new IllegalArgumentException(
                    String.format("Client rating must be between %d and %d, got %d",
                            MIN_RATING, MAX_RATING, rating));
        }
    }

    private static void validateComment(String comment) {
        if (comment == null || comment.isBlank()) {
            throw new IllegalArgumentException("Client comment is required");
        }
    }
}
