package com.veritrabajo.backend.jobmarketplace.infrastructure.persistence;

import com.veritrabajo.backend.jobmarketplace.domain.model.JobPost;
import com.veritrabajo.backend.jobmarketplace.domain.port.JobPostRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory repository implementation for the in-memory profile.
 */
@Repository
@Profile("in-memory")
public class InMemoryJobPostRepository implements JobPostRepository {

    private final Map<UUID, JobPost> storeById = new ConcurrentHashMap<>();

    @Override
    public Optional<JobPost> findById(UUID id) {
        return Optional.ofNullable(storeById.get(id));
    }

    @Override
    public JobPost save(JobPost jobPost) {
        storeById.put(jobPost.id(), jobPost);
        return jobPost;
    }

    @Override
    public List<JobPost> findOpenDemands() {
        return storeById.values()
                .stream()
                .filter(this::isOpenDemand)
                .toList();
    }

    private boolean isOpenDemand(JobPost jobPost) {
        return jobPost.selectedWorkerProfileId().isEmpty();
    }
}
