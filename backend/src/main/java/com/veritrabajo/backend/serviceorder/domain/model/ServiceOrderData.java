package com.veritrabajo.backend.serviceorder.domain.model;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

/**
 * Parameter object for ServiceOrder construction.
 */
record ServiceOrderData(
        UUID id,
        String clientId,
        String workerId,
        ServiceOrderStatus status,
        List<EvidencePhoto> photos) {

    public ServiceOrderData {
        Objects.requireNonNull(id, "Order id is required");
        Objects.requireNonNull(status, "Status is required");
        Objects.requireNonNull(photos, "Photos list is required");
        validatePartyId(clientId, "Client id");
        validatePartyId(workerId, "Worker id");
    }

    private static void validatePartyId(String partyId, String label) {
        if (partyId == null || partyId.isBlank()) {
            throw new IllegalArgumentException(label + " is required");
        }
    }
}
