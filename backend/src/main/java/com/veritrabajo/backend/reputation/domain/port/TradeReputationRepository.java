package com.veritrabajo.backend.reputation.domain.port;

import com.veritrabajo.backend.reputation.domain.model.TradeReputation;

import java.util.Optional;

/**
 * Repository port (outbound) for the {@link TradeReputation} aggregate root.
 * Defines persistence operations required by the domain.
 * Infrastructure adapters provide the actual storage implementation.
 */
public interface TradeReputationRepository {

    /**
     * Finds a trade reputation aggregate by the associated professional profile id.
     *
     * @param profileId the professional profile identifier
     * @return an optional containing the reputation if found
     */
    Optional<TradeReputation> findByProfileId(String profileId);

    /**
     * Persists a trade reputation aggregate.
     *
     * @param tradeReputation the aggregate to save
     * @return the persisted aggregate
     */
    TradeReputation save(TradeReputation tradeReputation);
}
