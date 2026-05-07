package com.veritrabajo.backend.jobmarketplace.application;

import com.veritrabajo.backend.jobmarketplace.domain.model.EstimatedBudget;
import com.veritrabajo.backend.jobmarketplace.domain.model.JobPostCreation;
import com.veritrabajo.backend.jobmarketplace.domain.model.TechnicalRequirements;
import com.veritrabajo.backend.jobmarketplace.domain.model.Urgency;

import java.math.BigDecimal;
import java.util.List;

public record CreateJobPostRequest(
        String clientId,
        List<String> technicalRequirements,
        BigDecimal minimumBudget,
        BigDecimal maximumBudget,
        Urgency urgency
) {
    public JobPostCreation toCreation() {
        TechnicalRequirements requirements = TechnicalRequirements.of(technicalRequirements);
        EstimatedBudget budget = new EstimatedBudget(minimumBudget, maximumBudget);
        return new JobPostCreation(clientId, requirements, budget, urgency);
    }
}
