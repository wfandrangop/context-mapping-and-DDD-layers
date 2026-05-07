package com.veritrabajo.backend.jobmarketplace.infrastructure.persistence;

import com.veritrabajo.backend.jobmarketplace.infrastructure.persistence.entity.JobPostEntity;
import org.springframework.context.annotation.Profile;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * Spring Data repository for JobPostEntity persistence operations.
 */
@Profile("!in-memory")
public interface SpringDataJobPostRepository extends JpaRepository<JobPostEntity, UUID> {

    List<JobPostEntity> findBySelectedWorkerProfileIdIsNull();
}
