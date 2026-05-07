package com.veritrabajo.backend.jobmarketplace.infrastructure.acl;

import com.veritrabajo.backend.jobmarketplace.domain.port.ReputationProvider;
import com.veritrabajo.backend.reputation.domain.port.TradeReputationRepository;
import org.springframework.stereotype.Component;

/**
 * Anti-corruption adapter for reading confidence scores from Reputation context.
 */
@Component
public class ReputationProviderAdapter implements ReputationProvider {

    private static final int DEFAULT_SCORE = 0;

    private final TradeReputationRepository repository;

    public ReputationProviderAdapter(TradeReputationRepository repository) {
        this.repository = repository;
    }

    @Override
    public int confidenceScore(String workerProfileId) {
        return repository.findByProfileId(workerProfileId)
                .map(reputation -> reputation.confidenceScore().value())
                .orElse(DEFAULT_SCORE);
    }
}
