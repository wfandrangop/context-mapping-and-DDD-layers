package com.veritrabajo.backend.serviceexecution.domain.event;

import java.util.Objects;
import java.util.UUID;

public record ServiceExecutionFinalized(UUID executionId, String workerId, String clientId) {

    public ServiceExecutionFinalized {
        Objects.requireNonNull(executionId, "Execution id is required");
        if (workerId == null || workerId.isBlank()) {
            throw new IllegalArgumentException("Worker id is required");
        }
        if (clientId == null || clientId.isBlank()) {
            throw new IllegalArgumentException("Client id is required");
        }
    }
}
