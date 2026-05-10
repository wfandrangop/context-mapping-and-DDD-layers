package com.veritrabajo.backend.jobmarketplace.domain.exception;

import java.util.UUID;

public class JobPostNotFoundException extends RuntimeException {

    public JobPostNotFoundException(UUID jobPostId) {
        super("Job demand not found: " + jobPostId);
    }
}
