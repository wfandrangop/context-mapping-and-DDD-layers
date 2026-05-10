package com.veritrabajo.backend.workerprofile.infrastructure.persistence;

import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface SpringWorkerProfileRepository
        extends JpaRepository<WorkerProfileEntity, String> {

    boolean existsByPhoneNumber(String phoneNumber);

    Optional<WorkerProfileEntity> findByAuthUserId(String authUserId);

    boolean existsByAuthUserId(String authUserId);
}
