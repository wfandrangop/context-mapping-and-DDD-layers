package com.veritrabajo.backend.jobmarketplace.infrastructure.persistence.entity;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

/**
 * JPA entity that persists the JobPost aggregate root.
 */
@Entity
@Table(name = "job_posts")
public class JobPostEntity {

    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(nullable = false)
    private String clientId;

    @Column(nullable = false)
    private String urgency;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal minimumBudget;

    @Column(nullable = false, precision = 12, scale = 2)
    private BigDecimal maximumBudget;

    @Column(nullable = false)
    private Instant createdAt;

    @Column
    private String selectedWorkerProfileId;

    @Version
    private Long version;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "job_post_technical_requirements",
            joinColumns = @JoinColumn(name = "job_post_id")
    )
    @Column(name = "required_skill", nullable = false)
    private List<String> technicalRequirements = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "job_post_applications",
            joinColumns = @JoinColumn(name = "job_post_id")
    )
    private List<JobApplicationEmbeddable> applications = new ArrayList<>();

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getClientId() {
        return clientId;
    }

    public void setClientId(String clientId) {
        this.clientId = clientId;
    }

    public String getUrgency() {
        return urgency;
    }

    public void setUrgency(String urgency) {
        this.urgency = urgency;
    }

    public BigDecimal getMinimumBudget() {
        return minimumBudget;
    }

    public void setMinimumBudget(BigDecimal minimumBudget) {
        this.minimumBudget = minimumBudget;
    }

    public BigDecimal getMaximumBudget() {
        return maximumBudget;
    }

    public void setMaximumBudget(BigDecimal maximumBudget) {
        this.maximumBudget = maximumBudget;
    }

    public Instant getCreatedAt() {
        return createdAt;
    }

    public void setCreatedAt(Instant createdAt) {
        this.createdAt = createdAt;
    }

    public String getSelectedWorkerProfileId() {
        return selectedWorkerProfileId;
    }

    public void setSelectedWorkerProfileId(String selectedWorkerProfileId) {
        this.selectedWorkerProfileId = selectedWorkerProfileId;
    }

    public List<String> getTechnicalRequirements() {
        return technicalRequirements;
    }

    public void setTechnicalRequirements(List<String> technicalRequirements) {
        this.technicalRequirements = technicalRequirements;
    }

    public List<JobApplicationEmbeddable> getApplications() {
        return applications;
    }

    public void setApplications(List<JobApplicationEmbeddable> applications) {
        this.applications = applications;
    }

    public Long getVersion() {
        return version;
    }
}
