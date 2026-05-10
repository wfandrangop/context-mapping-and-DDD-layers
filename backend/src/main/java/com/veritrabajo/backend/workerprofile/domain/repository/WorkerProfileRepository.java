package com.veritrabajo.backend.workerprofile.domain.repository;

import com.veritrabajo.backend.workerprofile.domain.model.AuthUserId;
import com.veritrabajo.backend.workerprofile.domain.model.WorkerId;
import com.veritrabajo.backend.workerprofile.domain.model.WorkerProfile;

import java.util.Optional;

public interface WorkerProfileRepository {

    WorkerProfile save(WorkerProfile profile);

    Optional<WorkerProfile> findById(WorkerId id);

    Optional<WorkerProfile> findByAuthUserId(AuthUserId authUserId);

    /**
     * Idempotency guard: indicates whether a profile already exists for the user.
     */
    boolean existsByAuthUserId(AuthUserId authUserId);

    /**
     * Checks whether a profile already exists for the phone number (duplicate guard).
     *
     * @param phoneNumber normalized phone to check
     * @return {@code true} when taken
     */
    boolean existsByPhoneNumber(String phoneNumber);
}
