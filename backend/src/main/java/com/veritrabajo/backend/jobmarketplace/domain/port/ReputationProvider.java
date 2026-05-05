package com.veritrabajo.backend.jobmarketplace.domain.port;

/**
 * Port used by JobMarketplace to query worker reputation scores.
 */
public interface ReputationProvider {

    int confidenceScore(String workerProfileId);
}
