package com.veritrabajo.backend.jobmarketplace.domain.port;

public interface ReputationProvider {

    int confidenceScore(String workerProfileId);
}
