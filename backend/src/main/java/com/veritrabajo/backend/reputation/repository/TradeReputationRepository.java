package com.veritrabajo.backend.reputation.repository;

import com.veritrabajo.backend.reputation.domain.model.TradeReputation;

import java.util.Optional;

/**
 * Repository interface for TradeReputation aggregate root.
 */
public interface TradeReputationRepository {

    Optional<TradeReputation> findByProfileId(String profileId);

    TradeReputation save(TradeReputation tradeReputation);
}
