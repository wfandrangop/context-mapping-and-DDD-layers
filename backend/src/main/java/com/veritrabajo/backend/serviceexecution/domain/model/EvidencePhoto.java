package com.veritrabajo.backend.serviceexecution.domain.model;

import java.time.Instant;
import java.util.Objects;
import java.util.UUID;

public record EvidencePhoto(UUID id, String url, Instant takenAt) {

    public EvidencePhoto {
        Objects.requireNonNull(id, "Photo id is required");
        if (url == null || url.isBlank()) {
            throw new IllegalArgumentException("Photo url is required");
        }
        Objects.requireNonNull(takenAt, "Taken-at timestamp is required");
    }

    public static EvidencePhoto of(String url) {
        return new EvidencePhoto(UUID.randomUUID(), url, Instant.now());
    }
}
