package com.veritrabajo.backend.jobmarketplace.domain.model;

import java.util.Objects;

public record JobPostCreation(
        String clientId,
        TechnicalRequirements technicalRequirements,
        EstimatedBudget estimatedBudget,
        Urgency urgency
) {
    public JobPostCreation {
        if (clientId == null || clientId.isBlank()) {
            throw new IllegalArgumentException("Client id is required");
        }
        Objects.requireNonNull(technicalRequirements, "Technical requirements are required");
        Objects.requireNonNull(estimatedBudget, "Estimated budget is required");
        Objects.requireNonNull(urgency, "Urgency is required");
    }
}
