package com.veritrabajo.backend.serviceexecution.infrastructure.persistence.entity;

import com.veritrabajo.backend.serviceexecution.domain.model.ServiceExecutionStatus;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

@Entity
@Table(name = "service_executions")
public class ServiceExecutionEntity {

    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(nullable = false)
    private String clientId;

    @Column(nullable = false)
    private String workerId;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private ServiceExecutionStatus status;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "service_execution_photos",
            joinColumns = @JoinColumn(name = "execution_id")
    )
    private List<EvidencePhotoEmbeddable> photos = new ArrayList<>();

    @Version
    private Long version;

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

    public String getWorkerId() {
        return workerId;
    }

    public void setWorkerId(String workerId) {
        this.workerId = workerId;
    }

    public ServiceExecutionStatus getStatus() {
        return status;
    }

    public void setStatus(ServiceExecutionStatus status) {
        this.status = status;
    }

    public List<EvidencePhotoEmbeddable> getPhotos() {
        return photos;
    }

    public void setPhotos(List<EvidencePhotoEmbeddable> photos) {
        this.photos = photos;
    }

    public Long getVersion() {
        return version;
    }
}
