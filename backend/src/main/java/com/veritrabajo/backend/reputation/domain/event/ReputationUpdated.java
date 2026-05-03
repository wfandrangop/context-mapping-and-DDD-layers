package com.veritrabajo.backend.reputation.domain.event;

import com.veritrabajo.backend.reputation.domain.model.ConfidenceScore;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Domain event raised when a trade professional's reputation score changes.
 * This event is internal to the reputation bounded context.
 */
public record ReputationUpdated(UUID reputationId, String profileId,
                                ConfidenceScore previousScore,
                                ConfidenceScore currentScore,
                                Instant occurredAt) {
    public ReputationUpdated(ReputationUpdateData data) {
        this(data.reputationId(), data.profileId(), data.previousScore(),
             data.currentScore(), Instant.now());
    }

    public ReputationUpdated {
        Objects.requireNonNull(reputationId, "Reputation id is required");
        if (profileId == null || profileId.isBlank()) {
            throw new IllegalArgumentException("Profile id is required");
        }
        Objects.requireNonNull(previousScore, "Previous score is required");
        Objects.requireNonNull(currentScore, "Current score is required");
        Objects.requireNonNull(occurredAt, "Occurred at timestamp is required");
    }
}
