package com.veritrabajo.backend.reputation.domain.model;

import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.UUID;

/**
 * Parameter Object for TradeReputation construction.
 * Encapsulates all required data for creating a TradeReputation aggregate.
 */
public record TradeReputationData(
        UUID id,
        String profileId,
        ConfidenceScore confidenceScore,
        Set<Badge> badges,
        ComplianceMetrics complianceMetrics,
        List<Review> reviews
) {
    
    public TradeReputationData {
        Objects.requireNonNull(id, "Reputation id is required");
        if (profileId == null || profileId.isBlank()) {
            throw new IllegalArgumentException("Profile id is required");
        }
        Objects.requireNonNull(confidenceScore, "Confidence score is required");
        Objects.requireNonNull(badges, "Badges are required");
        Objects.requireNonNull(complianceMetrics, "Compliance metrics are required");
        Objects.requireNonNull(reviews, "Reviews are required");
    }
}
