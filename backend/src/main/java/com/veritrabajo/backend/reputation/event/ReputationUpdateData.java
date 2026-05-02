package com.veritrabajo.backend.reputation.event;

import com.veritrabajo.backend.reputation.domain.model.ConfidenceScore;

import java.util.Objects;
import java.util.UUID;

/**
 * Parameter Object for ReputationUpdated construction.
 * Encapsulates the data required when a reputation score is updated.
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
