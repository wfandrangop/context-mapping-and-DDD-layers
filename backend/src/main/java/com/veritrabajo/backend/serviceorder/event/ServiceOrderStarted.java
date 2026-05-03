package com.veritrabajo.backend.serviceorder.event;

import java.util.Objects;
import java.util.UUID;

/**
 * Domain event published when a service order transitions to IN_PROCESS.
 */
public record ServiceOrderStarted(UUID orderId, String workerId, String clientId) {

    public ServiceOrderStarted {
        Objects.requireNonNull(orderId, "Order id is required");
        if (workerId == null || workerId.isBlank()) {
            throw new IllegalArgumentException("Worker id is required");
        }
        if (clientId == null || clientId.isBlank()) {
            throw new IllegalArgumentException("Client id is required");
        }
    }
}
