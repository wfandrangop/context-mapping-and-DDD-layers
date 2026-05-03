package com.veritrabajo.backend.reputation.domain.event;

import com.veritrabajo.backend.reputation.domain.model.ConfidenceScore;

import java.util.Objects;
import java.util.UUID;

/**
 * Parameter Object encapsulating the data required when a reputation score changes.
 * Used to construct {@link ReputationUpdated} domain events.
 */
public record ReputationUpdateData(UUID reputationId, String profileId,
                                   ConfidenceScore previousScore,
                                   ConfidenceScore currentScore) {

    public ReputationUpdateData {
        Objects.requireNonNull(reputationId, "Reputation id is required");
        if (profileId == null || profileId.isBlank()) {
            throw new IllegalArgumentException("Profile id is required");
        }
        Objects.requireNonNull(previousScore, "Previous score is required");
        Objects.requireNonNull(currentScore, "Current score is required");
    }
}
