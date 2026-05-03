package com.veritrabajo.backend.reputation.infrastructure.persistence;

import com.veritrabajo.backend.reputation.infrastructure.persistence.entity.TradeReputationEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data JPA repository for {@link TradeReputationEntity}.
 * Provides standard CRUD operations and custom query methods
 * for the persistence layer.
 */
public interface SpringDataTradeReputationRepository
        extends JpaRepository<TradeReputationEntity, UUID> {

    Optional<TradeReputationEntity> findByProfileId(
            String profileId
    );
}
