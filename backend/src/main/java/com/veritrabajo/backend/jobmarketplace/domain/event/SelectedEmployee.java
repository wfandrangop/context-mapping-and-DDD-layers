package com.veritrabajo.backend.jobmarketplace.domain.event;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public record SelectedEmployee(UUID jobPostId, String workerProfileId, Instant occurredAt) {

    public SelectedEmployee(UUID jobPostId, String workerProfileId) {
        this(jobPostId, workerProfileId, Instant.now());
    }

    public SelectedEmployee {
        Objects.requireNonNull(jobPostId, "Job post id is required");
        if (workerProfileId == null || workerProfileId.isBlank()) {
            throw new IllegalArgumentException("Worker profile id is required");
        }
        Objects.requireNonNull(occurredAt, "Occurred-at timestamp is required");
    }
}
