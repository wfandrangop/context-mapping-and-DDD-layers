package com.veritrabajo.backend.jobmarketplace.application;

import com.veritrabajo.backend.jobmarketplace.domain.event.NewDemandPublished;
import com.veritrabajo.backend.jobmarketplace.domain.event.SelectedEmployee;
import com.veritrabajo.backend.jobmarketplace.domain.exception.JobPostNotFoundException;
import com.veritrabajo.backend.jobmarketplace.domain.model.JobPost;
import com.veritrabajo.backend.jobmarketplace.domain.model.JobPostCreation;
import com.veritrabajo.backend.jobmarketplace.domain.port.DomainEventPublisher;
import com.veritrabajo.backend.jobmarketplace.domain.port.JobPostRepository;
import com.veritrabajo.backend.jobmarketplace.domain.port.ReputationProvider;
import com.veritrabajo.backend.jobmarketplace.domain.service.EmployeeSelectionService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
public class JobMarketplaceApplicationService {

    private final JobPostRepository repository;
    private final ReputationProvider reputationProvider;
    private final DomainEventPublisher eventPublisher;
    private final EmployeeSelectionService selectionService;

    public JobMarketplaceApplicationService(
            JobPostRepository repository,
            ReputationProvider reputationProvider,
            DomainEventPublisher eventPublisher
    ) {
        this.repository = repository;
        this.reputationProvider = reputationProvider;
        this.eventPublisher = eventPublisher;
        this.selectionService = new EmployeeSelectionService();
    }

    @Transactional
    public JobPost publishDemand(JobPostCreation creation) {
        JobPost saved = repository.save(JobPost.create(creation));
        NewDemandPublished event = saved.publishDemand();
        eventPublisher.publish(event);
        return saved;
    }

    @Transactional
    public JobPost apply(UUID jobPostId, String workerProfileId) {
        JobPost jobPost = findOrThrow(jobPostId);
        jobPost.apply(workerProfileId);
        return repository.save(jobPost);
    }

    @Transactional
    public JobPost select(UUID jobPostId, String workerProfileId) {
        JobPost jobPost = findOrThrow(jobPostId);
        String candidate = resolveCandidate(jobPost, workerProfileId);
        publishSelection(jobPost, candidate);
        return repository.save(jobPost);
    }

    public JobPost getById(UUID jobPostId) {
        return findOrThrow(jobPostId);
    }

    public List<JobPost> getOpenDemands() {
        return repository.findOpenDemands();
    }

    private JobPost findOrThrow(UUID jobPostId) {
        return repository.findById(jobPostId)
                .orElseThrow(() -> new JobPostNotFoundException(jobPostId));
    }

    private String resolveCandidate(JobPost jobPost, String workerProfileId) {
        if (workerProfileId != null && !workerProfileId.isBlank()) {
            return workerProfileId;
        }
        return selectionService.recommendWorker(jobPost, reputationProvider)
                .orElseThrow(() -> new IllegalStateException(
                        "No candidates available for selection"
                ));
    }

    private void publishSelection(JobPost jobPost, String workerProfileId) {
        SelectedEmployee event = jobPost.selectEmployee(workerProfileId)
                .orElseThrow(() -> new IllegalStateException(
                        "Worker already selected for this demand"
                ));
        eventPublisher.publish(event);
    }
}
