package com.veritrabajo.backend.jobmarketplace.domain.service;

import com.veritrabajo.backend.jobmarketplace.domain.model.JobPost;
import com.veritrabajo.backend.jobmarketplace.domain.port.ReputationProvider;

import java.util.Comparator;
import java.util.Optional;

/**
 * Stateless domain service that recommends the best candidate for a job demand.
 */
public class EmployeeSelectionService {

    public Optional<String> recommendWorker(
            JobPost jobPost,
            ReputationProvider reputationProvider
    ) {
        return jobPost.applicantProfileIds()
                .stream()
                .max(Comparator.comparingInt(reputationProvider::confidenceScore));
    }
}
