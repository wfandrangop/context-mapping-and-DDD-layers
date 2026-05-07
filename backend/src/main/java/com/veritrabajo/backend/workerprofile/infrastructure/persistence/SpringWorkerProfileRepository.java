package com.veritrabajo.backend.workerprofile.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Spring Data repository for {@link WorkerProfileEntity}; used only from infrastructure.
 */
public interface SpringWorkerProfileRepository
        extends JpaRepository<WorkerProfileEntity, String> {

    boolean existsByPhoneNumber(String phoneNumber);
}
