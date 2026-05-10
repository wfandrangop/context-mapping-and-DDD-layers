package com.veritrabajo.backend.workerprofile.infrastructure.acl;

import com.veritrabajo.backend.reputation.domain.port.TradeReputationRepository;
import com.veritrabajo.backend.workerprofile.domain.port.ReputationProvider;
import org.springframework.stereotype.Component;

/**
 * Anti-corruption adapter: translates reputation context data into the score
 * the workerprofile context needs, without coupling domain layers directly.
 */
@Component("workerProfileReputationProviderAdapter")
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
