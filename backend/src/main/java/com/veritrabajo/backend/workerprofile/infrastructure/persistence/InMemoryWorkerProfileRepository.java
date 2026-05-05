package com.veritrabajo.backend.workerprofile.infrastructure.persistence;

import com.veritrabajo.backend.workerprofile.domain.model.WorkerProfile;
import com.veritrabajo.backend.workerprofile.domain.repository.WorkerProfileRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory repository implementation for worker profiles in the in-memory profile.
 */
@Repository
@Profile("in-memory")
public class InMemoryWorkerProfileRepository implements WorkerProfileRepository {

    private final Map<String, WorkerProfile> storeById = new ConcurrentHashMap<>();
    private final Map<String, String> phoneToId = new ConcurrentHashMap<>();

    @Override
    public WorkerProfile save(WorkerProfile profile) {
        storeById.put(profile.getId(), profile);
        phoneToId.put(profile.getPhoneNumber(), profile.getId());
        return profile;
    }

    @Override
    public WorkerProfile findById(String id) {
        return storeById.get(id);
    }

    @Override
    public boolean existsByPhoneNumber(String phoneNumber) {
        return phoneToId.containsKey(phoneNumber);
    }
}
