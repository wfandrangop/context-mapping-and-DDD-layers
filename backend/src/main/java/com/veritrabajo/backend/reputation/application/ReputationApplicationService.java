package com.veritrabajo.backend.reputation.application;

import com.veritrabajo.backend.reputation.domain.model.TradeReputation;
import com.veritrabajo.backend.reputation.repository.TradeReputationRepository;
import org.springframework.stereotype.Service;

import java.util.Optional;

/**
 * Application service for managing trade reputation operations.
 */
@Service
public class ReputationApplicationService {

    private final TradeReputationRepository tradeReputationRepository;

    public ReputationApplicationService(TradeReputationRepository tradeReputationRepository) {
        this.tradeReputationRepository = tradeReputationRepository;
    }

    public TradeReputation createIfNotExists(String profileId) {
        return tradeReputationRepository
                .findByProfileId(profileId)
                .orElseGet(() -> tradeReputationRepository.save(
                        TradeReputation.createInitial(profileId)));
    }

    public Optional<TradeReputation> getByProfileId(String profileId) {
        return tradeReputationRepository.findByProfileId(profileId);
    }
}
