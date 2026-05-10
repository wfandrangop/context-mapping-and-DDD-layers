package com.veritrabajo.backend.jobmarketplace.domain.model;

import java.time.Instant;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

public record JobPostData(
        UUID id,
        String clientId,
        TechnicalRequirements technicalRequirements,
        EstimatedBudget estimatedBudget,
        Urgency urgency,
        Instant createdAt,
        List<JobApplication> applications,
        String selectedWorkerProfileId
) {
    public JobPostData {
        Objects.requireNonNull(id, "Job post id is required");
        if (clientId == null || clientId.isBlank()) {
            throw new IllegalArgumentException("Client id is required");
        }
        Objects.requireNonNull(technicalRequirements, "Technical requirements are required");
        Objects.requireNonNull(estimatedBudget, "Estimated budget is required");
        Objects.requireNonNull(urgency, "Urgency is required");
        Objects.requireNonNull(createdAt, "Created-at timestamp is required");
        Objects.requireNonNull(applications, "Applications list is required");
    }
}
