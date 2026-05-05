package com.veritrabajo.backend.workerprofile.domain.repository;

import com.veritrabajo.backend.workerprofile.domain.model.WorkerProfile;

/**
 * Persistence port for {@link WorkerProfile}. Domain depends only on this contract;
 * concrete adapters belong in infrastructure.
 */
public interface WorkerProfileRepository {

    /**
     * Persists (insert or update) a profile aggregate.
     *
     * @param profile aggregate snapshot to store
     * @return persisted aggregate (including generated columns if any)
     */
    WorkerProfile save(WorkerProfile profile);

    /**
     * Loads a profile by id.
     *
     * @param id aggregate identifier
     * @return matching profile or {@code null} when absent
     */
    WorkerProfile findById(String id);

    /**
     * Checks whether a profile already exists for the phone number (duplicate guard).
     *
     * @param phoneNumber normalized phone to check
     * @return {@code true} when taken
     */
    boolean existsByPhoneNumber(String phoneNumber);
}
