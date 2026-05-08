package com.veritrabajo.backend.reputation.application;

import com.veritrabajo.backend.reputation.domain.event.BadgeAwarded;
import com.veritrabajo.backend.reputation.domain.event.ReputationUpdated;
import com.veritrabajo.backend.reputation.domain.model.Badge;
import com.veritrabajo.backend.reputation.domain.model.ComplianceMetrics;
import com.veritrabajo.backend.reputation.domain.model.ConfidenceScore;
import com.veritrabajo.backend.reputation.domain.model.Review;
import com.veritrabajo.backend.reputation.domain.model.TradeReputation;
import com.veritrabajo.backend.reputation.domain.port.DomainEventPublisher;
import com.veritrabajo.backend.reputation.domain.port.TradeReputationRepository;
import com.veritrabajo.backend.reputation.domain.service.ReputationCalculator;
import com.veritrabajo.backend.shared.contract.serviceexecution.ServiceExecutionCompleted;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Propagation;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.Set;

@Service
public class ReputationApplicationService {

    private final TradeReputationRepository repository;
    private final ReputationCalculator calculator;
    private final DomainEventPublisher eventPublisher;

    public ReputationApplicationService(TradeReputationRepository repository,
                                        ReputationCalculator calculator,
                                        DomainEventPublisher eventPublisher) {
        this.repository = repository;
        this.calculator = calculator;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public TradeReputation createIfNotExists(String profileId) {
        return repository
                .findByProfileId(profileId)
                .orElseGet(() -> repository.save(
                        TradeReputation.createInitial(profileId)));
    }

    @Transactional(readOnly = true)
    public Optional<TradeReputation> getByProfileId(String profileId) {
        return repository.findByProfileId(profileId);
    }

    @Transactional(propagation = Propagation.REQUIRES_NEW)
    public void processServiceCompletion(ServiceExecutionCompleted event) {
        TradeReputation reputation = createIfNotExists(event.profileId());

        recordReviewFromExecution(reputation, event);
        updateComplianceFromExecution(reputation, event);
        recalculateScoreAndBadges(reputation);

        repository.save(reputation);
    }

    @Transactional
    public void recalculateReputation(String profileId) {
        TradeReputation reputation = repository.findByProfileId(profileId)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No reputation found for profile: " + profileId));

        recalculateScoreAndBadges(reputation);
        repository.save(reputation);
    }

    private void recordReviewFromExecution(TradeReputation reputation,
                                           ServiceExecutionCompleted event) {
        Review review = Review.create(event.clientComment(), event.clientRating());
        reputation.recordReview(review);
    }

    private void updateComplianceFromExecution(TradeReputation reputation,
                                               ServiceExecutionCompleted event) {
        ComplianceMetrics updated = event.completedSuccessfully()
                ? reputation.complianceMetrics().withSuccessfulJob()
                : reputation.complianceMetrics().withCancelledJob();
        reputation.updateMetrics(updated);
    }

    private void recalculateScoreAndBadges(TradeReputation reputation) {
        ConfidenceScore newScore = calculator.calculateScore(reputation);
        ReputationUpdated scoreEvent = reputation.updateScore(newScore);
        eventPublisher.publish(scoreEvent);

        Set<Badge> earnedBadges = calculator.calculateBadges(reputation);
        for (Badge badge : earnedBadges) {
            Optional<BadgeAwarded> badgeEvent = reputation.awardBadge(badge);
            badgeEvent.ifPresent(eventPublisher::publish);
        }
    }
}
