package com.veritrabajo.backend.workerprofile.domain.port;

public interface ReputationProvider {

    int confidenceScore(String workerProfileId);
}
