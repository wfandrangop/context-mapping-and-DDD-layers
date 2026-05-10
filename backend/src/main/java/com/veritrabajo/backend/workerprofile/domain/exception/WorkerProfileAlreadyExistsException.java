package com.veritrabajo.backend.workerprofile.domain.exception;

import com.veritrabajo.backend.workerprofile.domain.model.AuthUserId;

/**
 * Raised when a worker profile already exists for the given authenticated user.
 * Extends {@link IllegalStateException} so it is mapped to {@code 409 Conflict} by the
 * shared API exception handler.
 */
public class WorkerProfileAlreadyExistsException extends IllegalStateException {

    public WorkerProfileAlreadyExistsException(AuthUserId authUserId) {
        super("Worker profile already exists for authUserId=" + authUserId.value());
    }
}
