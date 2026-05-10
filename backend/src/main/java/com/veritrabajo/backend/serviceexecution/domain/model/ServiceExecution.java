package com.veritrabajo.backend.serviceexecution.domain.model;

import com.veritrabajo.backend.serviceexecution.domain.event.ServiceExecutionFinalized;
import com.veritrabajo.backend.serviceexecution.domain.event.ServiceExecutionStarted;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/** State machine: STARTED → IN_PROCESS → FINALIZED. */
public final class ServiceExecution {

    private final UUID id;
    private final String clientId;
    private final String workerId;
    private ServiceExecutionStatus status;
    private final List<EvidencePhoto> photos;

    private ServiceExecution(ServiceExecutionData data) {
        this.id = data.id();
        this.clientId = data.clientId();
        this.workerId = data.workerId();
        this.status = data.status();
        this.photos = new ArrayList<>(data.photos());
    }

    public static ServiceExecution create(String clientId, String workerId) {
        return new ServiceExecution(new ServiceExecutionData(
                UUID.randomUUID(), clientId, workerId,
                ServiceExecutionStatus.STARTED, List.of()
        ));
    }

    public static ServiceExecution rehydrate(ServiceExecutionData data) {
        Objects.requireNonNull(data, "Service execution data is required");
        return new ServiceExecution(data);
    }

    public UUID id() {
        return id;
    }

    public String clientId() {
        return clientId;
    }

    public String workerId() {
        return workerId;
    }

    public ServiceExecutionStatus status() {
        return status;
    }

    public List<EvidencePhoto> photos() {
        return List.copyOf(photos);
    }

    public ServiceExecutionStarted begin() {
        if (status != ServiceExecutionStatus.STARTED) {
            throw new IllegalStateException("Execution must be STARTED to begin");
        }
        status = ServiceExecutionStatus.IN_PROCESS;
        return new ServiceExecutionStarted(id, workerId, clientId);
    }

    public void addPhoto(EvidencePhoto photo) {
        photos.add(Objects.requireNonNull(photo, "Photo is required"));
    }

    public ServiceExecutionFinalized complete() {
        validateCompletable();
        status = ServiceExecutionStatus.FINALIZED;
        return new ServiceExecutionFinalized(id, workerId, clientId);
    }

    public void dispute() {
        if (status != ServiceExecutionStatus.FINALIZED) {
            throw new IllegalStateException("Execution must be FINALIZED to dispute");
        }
        status = ServiceExecutionStatus.DISPUTED;
    }

    private void validateCompletable() {
        if (status != ServiceExecutionStatus.IN_PROCESS) {
            throw new IllegalStateException("Execution must be IN_PROCESS to complete");
        }
        if (photos.isEmpty()) {
            throw new IllegalStateException("At least one evidence photo is required");
        }
    }

    @Override
    public String toString() {
        return "ServiceExecution{id=" + id + ", status=" + status + "}";
    }
}
