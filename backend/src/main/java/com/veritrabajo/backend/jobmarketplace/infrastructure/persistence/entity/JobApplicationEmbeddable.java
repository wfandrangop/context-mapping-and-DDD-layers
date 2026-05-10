package com.veritrabajo.backend.jobmarketplace.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.time.Instant;
import java.util.UUID;

@Embeddable
public class JobApplicationEmbeddable {

    @Column(name = "application_id", nullable = false)
    private UUID applicationId;

    @Column(name = "worker_profile_id", nullable = false)
    private String workerProfileId;

    @Column(name = "applied_at", nullable = false)
    private Instant appliedAt;

    public UUID getApplicationId() {
        return applicationId;
    }

    public void setApplicationId(UUID applicationId) {
        this.applicationId = applicationId;
    }

    public String getWorkerProfileId() {
        return workerProfileId;
    }

    public void setWorkerProfileId(String workerProfileId) {
        this.workerProfileId = workerProfileId;
    }

    public Instant getAppliedAt() {
        return appliedAt;
    }

    public void setAppliedAt(Instant appliedAt) {
        this.appliedAt = appliedAt;
    }
}
