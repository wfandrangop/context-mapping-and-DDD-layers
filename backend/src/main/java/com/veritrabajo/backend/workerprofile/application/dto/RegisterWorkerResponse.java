package com.veritrabajo.backend.workerprofile.application.dto;

public class RegisterWorkerResponse {

    private final String message;
    private final String profileId;

    private RegisterWorkerResponse(String message, String profileId) {
        this.message = message;
        this.profileId = profileId;
    }

    public static RegisterWorkerResponse success(String profileId) {
        return new RegisterWorkerResponse(
                "Profile registered successfully",
                profileId
        );
    }

    public String getMessage() {
        return message;
    }

    public String getProfileId() {
        return profileId;
    }
}
