package com.veritrabajo.backend.reputation.infrastructure.persistence;

import com.veritrabajo.backend.reputation.domain.model.TradeReputation;
import com.veritrabajo.backend.reputation.domain.port.TradeReputationRepository;
import com.veritrabajo.backend.reputation.infrastructure.persistence.entity.TradeReputationEntity;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class JpaTradeReputationRepository
        implements TradeReputationRepository {

    private final SpringDataTradeReputationRepository jpaRepo;

    public JpaTradeReputationRepository(
            SpringDataTradeReputationRepository jpaRepo) {
        this.jpaRepo = jpaRepo;
    }

    @Override
    public Optional<TradeReputation> findByProfileId(
            String profileId) {
        return jpaRepo.findByProfileId(profileId)
                .map(TradeReputationMapper::toDomain);
    }

    @Override
    public TradeReputation save(
            TradeReputation tradeReputation) {
        TradeReputationEntity entity = jpaRepo
                .findByProfileId(tradeReputation.profileId())
                .orElseGet(TradeReputationEntity::new);
        TradeReputationMapper.updateEntity(
                entity, tradeReputation);
        jpaRepo.save(entity);
        return tradeReputation;
    }
}
