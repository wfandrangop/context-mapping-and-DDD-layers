package com.veritrabajo.backend.jobmarketplace.domain.model;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

/**
 * Entity representing a worker application to a job demand.
 */
public final class JobApplication {

    private final UUID id;
    private final String workerProfileId;
    private final Instant appliedAt;

    private JobApplication(UUID id, String workerProfileId, Instant appliedAt) {
        this.id = Objects.requireNonNull(id, "Application id is required");
        this.workerProfileId = validateWorkerProfileId(workerProfileId);
        this.appliedAt = Objects.requireNonNull(appliedAt, "Applied-at timestamp is required");
    }

    public static JobApplication create(String workerProfileId) {
        return new JobApplication(UUID.randomUUID(), workerProfileId, Instant.now());
    }

    public static JobApplication rehydrate(UUID id, String workerProfileId, Instant appliedAt) {
        return new JobApplication(id, workerProfileId, appliedAt);
    }

    public UUID id() {
        return id;
    }

    public String workerProfileId() {
        return workerProfileId;
    }

    public Instant appliedAt() {
        return appliedAt;
    }

    private static String validateWorkerProfileId(String workerProfileId) {
        if (workerProfileId == null || workerProfileId.isBlank()) {
            throw new IllegalArgumentException("Worker profile id is required");
        }
        return workerProfileId;
    }
}
