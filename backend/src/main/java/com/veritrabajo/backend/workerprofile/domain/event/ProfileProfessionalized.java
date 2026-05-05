package com.veritrabajo.backend.workerprofile.domain.event;

import java.time.Instant;

/**
 * Raised after AI enrichment completes so other bounded contexts (e.g. reputation) can react.
 */
public final class ProfileProfessionalized {

    private final String workerProfileId;
    private final String fullName;
    private final Instant occurredAt;

    private ProfileProfessionalized(
            String workerProfileId,
            String fullName
    ) {
        this.workerProfileId = workerProfileId;
        this.fullName = fullName;
        this.occurredAt = Instant.now();
    }

    public static ProfileProfessionalized of(
            String workerProfileId,
            String fullName
    ) {
        if (workerProfileId == null || workerProfileId.isBlank()) {
            throw new IllegalArgumentException(
                    "Profile id cannot be blank"
            );
        }
        if (fullName == null || fullName.isBlank()) {
            throw new IllegalArgumentException(
                    "Worker full name cannot be blank"
            );
        }
        return new ProfileProfessionalized(workerProfileId, fullName);
    }

    public String getWorkerProfileId() {
        return workerProfileId;
    }

    public String getFullName() {
        return fullName;
    }

    public Instant getOccurredAt() {
        return occurredAt;
    }

    @Override
    public String toString() {
        return "ProfileProfessionalized{workerProfileId='" + workerProfileId
                + "', fullName='" + fullName
                + "', occurredAt=" + occurredAt + "}";
    }
}
