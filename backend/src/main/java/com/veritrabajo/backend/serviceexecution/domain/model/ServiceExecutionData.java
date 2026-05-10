package com.veritrabajo.backend.serviceexecution.domain.model;

import java.util.List;
import java.util.Objects;
import java.util.UUID;

public record ServiceExecutionData(
        UUID id,
        String clientId,
        String workerId,
        ServiceExecutionStatus status,
        List<EvidencePhoto> photos) {

    public ServiceExecutionData {
        Objects.requireNonNull(id, "Execution id is required");
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
