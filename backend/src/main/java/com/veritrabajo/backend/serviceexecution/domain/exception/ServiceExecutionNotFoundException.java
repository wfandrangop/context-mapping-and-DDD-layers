package com.veritrabajo.backend.serviceexecution.domain.exception;

import java.util.UUID;

public class ServiceExecutionNotFoundException extends RuntimeException {

    public ServiceExecutionNotFoundException(UUID executionId) {
        super("Execution not found: " + executionId);
    }
}
