package com.veritrabajo.backend.customer.domain.event;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public record ServiceRequestedByCustomer(
        UUID customerId,
        UUID jobPostId,
        UUID addressId,
        Instant occurredAt
) {
    public ServiceRequestedByCustomer(UUID customerId, UUID jobPostId, UUID addressId) {
        this(customerId, jobPostId, addressId, Instant.now());
    }

    public ServiceRequestedByCustomer {
        Objects.requireNonNull(customerId, "Customer id is required");
        Objects.requireNonNull(jobPostId, "Job post id is required");
        Objects.requireNonNull(addressId, "Address id is required");
        Objects.requireNonNull(occurredAt, "Occurred-at timestamp is required");
    }
}
