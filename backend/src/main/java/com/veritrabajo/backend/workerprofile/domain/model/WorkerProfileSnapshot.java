package com.veritrabajo.backend.workerprofile.domain.model;

public record WorkerProfileSnapshot(
        WorkerId id,
        AuthUserId authUserId,
        String fullName,
        String phoneNumber
) { }
