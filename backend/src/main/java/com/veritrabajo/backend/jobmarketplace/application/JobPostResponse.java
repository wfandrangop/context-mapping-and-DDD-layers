package com.veritrabajo.backend.jobmarketplace.application;

import com.veritrabajo.backend.jobmarketplace.domain.model.JobPost;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record JobPostResponse(
        UUID id,
        String clientId,
        List<String> technicalRequirements,
        BigDecimal minimumBudget,
        BigDecimal maximumBudget,
        String urgency,
        List<String> applicantProfileIds,
        String selectedWorkerProfileId,
        Instant createdAt
) {

    public static JobPostResponse from(JobPost jobPost) {
        String selected = jobPost.selectedWorkerProfileId().orElse(null);
        return new JobPostResponse(
                jobPost.id(),
                jobPost.clientId(),
                jobPost.technicalRequirements().skills(),
                jobPost.estimatedBudget().minimum(),
                jobPost.estimatedBudget().maximum(),
                jobPost.urgency().name(),
                jobPost.applicantProfileIds(),
                selected,
                jobPost.createdAt()
        );
    }
}
