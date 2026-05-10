package com.veritrabajo.backend.reputation.application;

import com.veritrabajo.backend.reputation.domain.model.Badge;
import com.veritrabajo.backend.reputation.domain.model.TradeReputation;

import java.util.Set;

public record TradeReputationResponse(
        String profileId,
        int confidenceScore,
        int successfulJobs,
        int cancelledJobs,
        Set<Badge> badges,
        int reviewCount) {

    public static TradeReputationResponse from(TradeReputation reputation) {
        return new TradeReputationResponse(
                reputation.profileId(),
                reputation.confidenceScore().value(),
                reputation.complianceMetrics().successfulJobs(),
                reputation.complianceMetrics().cancelledJobs(),
                reputation.badges(),
                reputation.reviews().size()
        );
    }
}
