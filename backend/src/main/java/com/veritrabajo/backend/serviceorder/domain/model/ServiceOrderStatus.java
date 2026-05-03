package com.veritrabajo.backend.serviceorder.domain.model;

/**
 * Lifecycle states for a ServiceOrder aggregate.
 */
public enum ServiceOrderStatus {
    STARTED,
    IN_PROCESS,
    FINALIZED
}
