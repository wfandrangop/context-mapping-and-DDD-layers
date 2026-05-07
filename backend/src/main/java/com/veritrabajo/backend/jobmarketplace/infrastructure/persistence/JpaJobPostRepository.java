package com.veritrabajo.backend.jobmarketplace.infrastructure.persistence;

import com.veritrabajo.backend.jobmarketplace.domain.model.JobPost;
import com.veritrabajo.backend.jobmarketplace.domain.port.JobPostRepository;
import com.veritrabajo.backend.jobmarketplace.infrastructure.persistence.entity.JobPostEntity;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * JPA-backed repository implementation for JobPost aggregate persistence.
 */
@Repository
@Profile("!in-memory")
public class JpaJobPostRepository implements JobPostRepository {

    private final SpringDataJobPostRepository springDataRepository;

    public JpaJobPostRepository(SpringDataJobPostRepository springDataRepository) {
        this.springDataRepository = springDataRepository;
    }

    @Override
    public Optional<JobPost> findById(UUID id) {
        return springDataRepository.findById(id)
                .map(JobPostMapper::toDomain);
    }

    @Override
    public JobPost save(JobPost jobPost) {
        JobPostEntity entity = springDataRepository.findById(jobPost.id())
                .orElseGet(JobPostEntity::new);
        JobPostMapper.updateEntity(entity, jobPost);
        springDataRepository.save(entity);
        return jobPost;
    }

    @Override
    public List<JobPost> findOpenDemands() {
        return springDataRepository.findBySelectedWorkerProfileIdIsNull()
                .stream()
                .map(JobPostMapper::toDomain)
                .toList();
    }
}
