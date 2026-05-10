package com.veritrabajo.backend.jobmarketplace.domain.event;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public record NewDemandPublished(UUID jobPostId, String clientId, Instant occurredAt) {

    public NewDemandPublished(UUID jobPostId, String clientId) {
        this(jobPostId, clientId, Instant.now());
    }

    public NewDemandPublished {
        Objects.requireNonNull(jobPostId, "Job post id is required");
        if (clientId == null || clientId.isBlank()) {
            throw new IllegalArgumentException("Client id is required");
        }
        Objects.requireNonNull(occurredAt, "Occurred-at timestamp is required");
    }
}
