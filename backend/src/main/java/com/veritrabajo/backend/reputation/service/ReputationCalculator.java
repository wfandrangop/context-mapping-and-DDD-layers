package com.veritrabajo.backend.reputation.service;

import com.veritrabajo.backend.reputation.domain.model.Badge;
import com.veritrabajo.backend.reputation.domain.model.ConfidenceScore;
import com.veritrabajo.backend.reputation.domain.model.TradeReputation;

import java.util.Set;

/**
 * Domain service contract for calculating reputation metrics.
 * Implementations should contain the business logic for score and badge calculations.
 */
public interface ReputationCalculator {

    ConfidenceScore calculateScore(TradeReputation reputation);

    Set<Badge> calculateBadges(TradeReputation reputation);
}
