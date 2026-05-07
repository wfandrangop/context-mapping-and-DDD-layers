package com.veritrabajo.backend.jobmarketplace.infrastructure.persistence;

import com.veritrabajo.backend.jobmarketplace.domain.model.EstimatedBudget;
import com.veritrabajo.backend.jobmarketplace.domain.model.JobApplication;
import com.veritrabajo.backend.jobmarketplace.domain.model.JobPost;
import com.veritrabajo.backend.jobmarketplace.domain.model.JobPostData;
import com.veritrabajo.backend.jobmarketplace.domain.model.TechnicalRequirements;
import com.veritrabajo.backend.jobmarketplace.domain.model.Urgency;
import com.veritrabajo.backend.jobmarketplace.infrastructure.persistence.entity.JobApplicationEmbeddable;
import com.veritrabajo.backend.jobmarketplace.infrastructure.persistence.entity.JobPostEntity;

import java.util.ArrayList;
import java.util.List;

/**
 * Mapper for translating between JobPost aggregate and its JPA entity.
 */
public final class JobPostMapper {

    private JobPostMapper() {
        // Utility class
    }

    public static JobPost toDomain(JobPostEntity entity) {
        JobPostData data = new JobPostData(
                entity.getId(),
                entity.getClientId(),
                TechnicalRequirements.of(entity.getTechnicalRequirements()),
                new EstimatedBudget(entity.getMinimumBudget(), entity.getMaximumBudget()),
                Urgency.valueOf(entity.getUrgency()),
                entity.getCreatedAt(),
                toDomainApplications(entity.getApplications()),
                entity.getSelectedWorkerProfileId()
        );
        return JobPost.rehydrate(data);
    }

    public static void updateEntity(JobPostEntity target, JobPost source) {
        target.setId(source.id());
        target.setClientId(source.clientId());
        target.setUrgency(source.urgency().name());
        target.setMinimumBudget(source.estimatedBudget().minimum());
        target.setMaximumBudget(source.estimatedBudget().maximum());
        target.setCreatedAt(source.createdAt());
        target.setTechnicalRequirements(
                new ArrayList<>(source.technicalRequirements().skills()));
        target.setSelectedWorkerProfileId(
                source.selectedWorkerProfileId().orElse(null));
        target.setApplications(
                toEntityApplications(source.applications()));
    }

    private static List<JobApplication> toDomainApplications(
            List<JobApplicationEmbeddable> source
    ) {
        return new ArrayList<>(source.stream()
                .map(JobPostMapper::toDomainApplication)
                .toList());
    }

    private static JobApplication toDomainApplication(JobApplicationEmbeddable source) {
        return JobApplication.rehydrate(
                source.getApplicationId(),
                source.getWorkerProfileId(),
                source.getAppliedAt()
        );
    }

    private static List<JobApplicationEmbeddable> toEntityApplications(
            List<JobApplication> source
    ) {
        return new ArrayList<>(source.stream()
                .map(JobPostMapper::toEmbeddable)
                .toList());
    }

    private static JobApplicationEmbeddable toEmbeddable(JobApplication source) {
        JobApplicationEmbeddable embeddable = new JobApplicationEmbeddable();
        embeddable.setApplicationId(source.id());
        embeddable.setWorkerProfileId(source.workerProfileId());
        embeddable.setAppliedAt(source.appliedAt());
        return embeddable;
    }
}
