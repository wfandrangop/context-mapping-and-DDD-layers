package com.veritrabajo.backend.jobmarketplace.domain.port;

import com.veritrabajo.backend.jobmarketplace.domain.model.JobPost;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface JobPostRepository {

    Optional<JobPost> findById(UUID id);

    JobPost save(JobPost jobPost);

    List<JobPost> findOpenDemands();
}
