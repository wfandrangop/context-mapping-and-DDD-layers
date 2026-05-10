package com.veritrabajo.backend.reputation.domain.port;

import com.veritrabajo.backend.reputation.domain.model.TradeReputation;

import java.util.Optional;

public interface TradeReputationRepository {

    Optional<TradeReputation> findByProfileId(String profileId);

    TradeReputation save(TradeReputation tradeReputation);
}
