package com.veritrabajo.backend.jobmarketplace.domain.model;

import com.veritrabajo.backend.jobmarketplace.domain.event.NewDemandPublished;
import com.veritrabajo.backend.jobmarketplace.domain.event.SelectedEmployee;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public final class JobPost {

    private final UUID id;
    private final String clientId;
    private final TechnicalRequirements technicalRequirements;
    private final EstimatedBudget estimatedBudget;
    private final Urgency urgency;
    private final Instant createdAt;
    private final List<JobApplication> applications;
    private String selectedWorkerProfileId;

    private JobPost(JobPostData data) {
        this.id = Objects.requireNonNull(data.id(), "Job post id is required");
        this.clientId = validateClientId(data.clientId());
        this.technicalRequirements = Objects.requireNonNull(
                data.technicalRequirements(), "Technical requirements are required"
        );
        this.estimatedBudget = Objects.requireNonNull(data.estimatedBudget(),
                "Estimated budget is required");
        this.urgency = Objects.requireNonNull(data.urgency(), "Urgency is required");
        this.createdAt = Objects.requireNonNull(
                data.createdAt(),
                "Created-at timestamp is required"
        );
        this.applications = new ArrayList<>(Objects.requireNonNull(data.applications(),
                "Applications list is required"));
        this.selectedWorkerProfileId = normalizeSelectedWorker(data.selectedWorkerProfileId());
    }

    public static JobPost create(JobPostCreation creation) {
        Objects.requireNonNull(creation, "Job post creation is required");
        JobPostData data = new JobPostData(
                UUID.randomUUID(),
                creation.clientId(),
                creation.technicalRequirements(),
                creation.estimatedBudget(),
                creation.urgency(),
                Instant.now(),
                List.of(),
                null
        );
        return new JobPost(data);
    }

    public static JobPost rehydrate(JobPostData data) {
        Objects.requireNonNull(data, "Job post data is required");
        return new JobPost(data);
    }

    public UUID id() {
        return id;
    }

    public String clientId() {
        return clientId;
    }

    public TechnicalRequirements technicalRequirements() {
        return technicalRequirements;
    }

    public EstimatedBudget estimatedBudget() {
        return estimatedBudget;
    }

    public Urgency urgency() {
        return urgency;
    }

    public Instant createdAt() {
        return createdAt;
    }

    public List<JobApplication> applications() {
        return List.copyOf(applications);
    }

    public Optional<String> selectedWorkerProfileId() {
        return Optional.ofNullable(selectedWorkerProfileId);
    }

    public List<String> applicantProfileIds() {
        return applications.stream().map(JobApplication::workerProfileId).toList();
    }

    public NewDemandPublished publishDemand() {
        return new NewDemandPublished(id, clientId);
    }

    public void apply(String workerProfileId) {
        String normalizedWorkerProfileId = validateWorkerProfileId(workerProfileId);
        validateDemandOpen();
        validateNotAlreadyApplied(normalizedWorkerProfileId);
        applications.add(JobApplication.create(normalizedWorkerProfileId));
    }

    public Optional<SelectedEmployee> selectEmployee(String workerProfileId) {
        String normalizedWorkerProfileId = validateWorkerProfileId(workerProfileId);
        validateApplicantExists(normalizedWorkerProfileId);
        if (Objects.equals(selectedWorkerProfileId, normalizedWorkerProfileId)) {
            return Optional.empty();
        }
        selectedWorkerProfileId = normalizedWorkerProfileId;
        return Optional.of(new SelectedEmployee(id, normalizedWorkerProfileId));
    }

    public JobPostData toData() {
        return new JobPostData(
                id,
                clientId,
                technicalRequirements,
                estimatedBudget,
                urgency,
                createdAt,
                List.copyOf(applications),
                selectedWorkerProfileId
        );
    }

    private void validateDemandOpen() {
        if (selectedWorkerProfileId != null) {
            throw new IllegalStateException("Cannot apply to a demand with a selected employee");
        }
    }

    private void validateNotAlreadyApplied(String workerProfileId) {
        boolean alreadyApplied = applicantProfileIds().contains(workerProfileId);
        if (alreadyApplied) {
            throw new IllegalArgumentException("Worker already applied to this demand");
        }
    }

    private void validateApplicantExists(String workerProfileId) {
        boolean isApplicant = applicantProfileIds().contains(workerProfileId);
        if (!isApplicant) {
            throw new IllegalArgumentException("Worker must apply before selection");
        }
    }

    private static String validateClientId(String clientId) {
        if (clientId == null || clientId.isBlank()) {
            throw new IllegalArgumentException("Client id is required");
        }
        return clientId;
    }

    private static String validateWorkerProfileId(String workerProfileId) {
        if (workerProfileId == null || workerProfileId.isBlank()) {
            throw new IllegalArgumentException("Worker profile id is required");
        }
        return workerProfileId;
    }

    private static String normalizeSelectedWorker(String selectedWorkerProfileId) {
        if (selectedWorkerProfileId == null || selectedWorkerProfileId.isBlank()) {
            return null;
        }
        return selectedWorkerProfileId;
    }
}
