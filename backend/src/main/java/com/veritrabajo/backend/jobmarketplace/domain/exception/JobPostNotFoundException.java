package com.veritrabajo.backend.jobmarketplace.domain.exception;

import java.util.UUID;

/**
 * Exception thrown when a job demand cannot be found.
 */
public class JobPostNotFoundException extends RuntimeException {

    public JobPostNotFoundException(UUID jobPostId) {
        super("Job demand not found: " + jobPostId);
    }
}
