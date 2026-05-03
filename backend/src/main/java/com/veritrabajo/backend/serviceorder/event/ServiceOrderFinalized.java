package com.veritrabajo.backend.serviceorder.event;

import java.util.Objects;
import java.util.UUID;

/**
 * Domain event published when a service order is completed and finalized.
 */
public record ServiceOrderFinalized(UUID orderId, String workerId, String clientId) {

    public ServiceOrderFinalized {
        Objects.requireNonNull(orderId, "Order id is required");
        if (workerId == null || workerId.isBlank()) {
            throw new IllegalArgumentException("Worker id is required");
        }
        if (clientId == null || clientId.isBlank()) {
            throw new IllegalArgumentException("Client id is required");
        }
    }
}
