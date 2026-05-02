package com.veritrabajo.backend.reputation.infraestructure.persistence;

import com.veritrabajo.backend.reputation.domain.model.TradeReputation;
import com.veritrabajo.backend.reputation.repository.TradeReputationRepository;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory implementation of TradeReputationRepository.
 * Stores TradeReputation aggregates in a ConcurrentHashMap.
 */
@Repository
public class InMemoryTradeReputationRepository implements TradeReputationRepository {

    private final Map<String, TradeReputation> store = new ConcurrentHashMap<>();

    @Override
    public Optional<TradeReputation> findByProfileId(String profileId) {
        return Optional.ofNullable(store.get(profileId));
    }

    @Override
    public TradeReputation save(TradeReputation tradeReputation) {
        store.put(tradeReputation.profileId(), tradeReputation);
        return tradeReputation;
    }
}
