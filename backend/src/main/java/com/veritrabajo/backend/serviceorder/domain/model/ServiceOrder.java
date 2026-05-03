package com.veritrabajo.backend.serviceorder.domain.model;

import com.veritrabajo.backend.serviceorder.event.ServiceOrderFinalized;
import com.veritrabajo.backend.serviceorder.event.ServiceOrderStarted;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Aggregate Root representing a service order between a client and a worker.
 * Follows a strict status state machine: STARTED → IN_PROCESS → FINALIZED.
 */
public final class ServiceOrder {

    private final UUID id;
    private final String clientId;
    private final String workerId;
    private ServiceOrderStatus status;
    private final List<EvidencePhoto> photos;

    private ServiceOrder(ServiceOrderData data) {
        this.id = data.id();
        this.clientId = data.clientId();
        this.workerId = data.workerId();
        this.status = data.status();
        this.photos = new ArrayList<>(data.photos());
    }

    public static ServiceOrder create(String clientId, String workerId) {
        return new ServiceOrder(new ServiceOrderData(
                UUID.randomUUID(), clientId, workerId,
                ServiceOrderStatus.STARTED, List.of()
        ));
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

    public ServiceOrderStatus status() {
        return status;
    }

    public List<EvidencePhoto> photos() {
        return List.copyOf(photos);
    }

    public ServiceOrderStarted begin() {
        if (status != ServiceOrderStatus.STARTED) {
            throw new IllegalStateException("Order must be STARTED to begin");
        }
        status = ServiceOrderStatus.IN_PROCESS;
        return new ServiceOrderStarted(id, workerId, clientId);
    }

    public void addPhoto(EvidencePhoto photo) {
        photos.add(Objects.requireNonNull(photo, "Photo is required"));
    }

    public ServiceOrderFinalized complete() {
        validateCompletable();
        status = ServiceOrderStatus.FINALIZED;
        return new ServiceOrderFinalized(id, workerId, clientId);
    }

    private void validateCompletable() {
        if (status != ServiceOrderStatus.IN_PROCESS) {
            throw new IllegalStateException("Order must be IN_PROCESS to complete");
        }
        if (photos.isEmpty()) {
            throw new IllegalStateException("At least one evidence photo is required");
        }
    }

    @Override
    public String toString() {
        return "ServiceOrder{id=" + id + ", status=" + status + "}";
    }
}
