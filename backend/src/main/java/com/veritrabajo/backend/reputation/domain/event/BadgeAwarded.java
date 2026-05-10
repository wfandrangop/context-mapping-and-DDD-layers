package com.veritrabajo.backend.reputation.domain.event;

import com.veritrabajo.backend.reputation.domain.model.Badge;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public record BadgeAwarded(
    UUID reputationId,
    String profileId,
    Badge badge,
    Instant occurredAt
) {
    public BadgeAwarded(UUID reputationId, String profileId, Badge badge) {
        this(reputationId, profileId, badge, Instant.now());
    }

    public BadgeAwarded {
        Objects.requireNonNull(reputationId, "Reputation id is required");
        if (profileId == null || profileId.isBlank()) {
            throw new IllegalArgumentException("Profile id is required");
        }
        Objects.requireNonNull(badge, "Badge is required");
        Objects.requireNonNull(occurredAt, "Occurred at timestamp is required");
    }
}
