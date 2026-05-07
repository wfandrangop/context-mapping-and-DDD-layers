package com.veritrabajo.backend.customer.domain.model;

import java.time.Instant;
import java.util.List;
import java.util.UUID;

/**
 * Data carrier used for rehydrating a Customer aggregate from persistence.
 */
public record CustomerData(
        UUID id,
        String name,
        Instant registrationDate,
        CustomerStatus status,
        ContactInfo contactInfo,
        List<SavedAddress> addresses,
        ClientPreferences preferences
) { }
