package com.veritrabajo.backend.workerprofile.application.dto;

/**
 * Response returned after a successful worker registration (success message and generated profile
 * id).
 */
public class RegisterWorkerResponse {

    private final String message;
    private final String profileId;

    private RegisterWorkerResponse(String message, String profileId) {
        this.message = message;
        this.profileId = profileId;
    }

    /**
     * Builds a success response for the given persisted profile id.
     *
     * @param profileId identifier assigned to the new profile
     */
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
