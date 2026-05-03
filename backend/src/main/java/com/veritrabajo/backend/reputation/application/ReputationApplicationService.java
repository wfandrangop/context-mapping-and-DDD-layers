package com.veritrabajo.backend.reputation.application;

import com.veritrabajo.backend.reputation.application.integration.ServiceExecutionCompleted;
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
import org.springframework.stereotype.Service;

import java.util.Optional;
import java.util.Set;

/**
 * Application service orchestrating reputation use cases.
 * Coordinates domain objects, domain services, and infrastructure ports
 * without containing business logic itself.
 */
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

    /**
     * Creates an initial reputation for a professional profile if one does not
     * already exist.
     *
     * @param profileId the professional profile identifier
     * @return the existing or newly created reputation
     */
    public TradeReputation createIfNotExists(String profileId) {
        return repository
                .findByProfileId(profileId)
                .orElseGet(() -> repository.save(
                        TradeReputation.createInitial(profileId)));
    }

    /**
     * Retrieves the reputation for a given professional profile.
     *
     * @param profileId the professional profile identifier
     * @return an optional containing the reputation if found
     */
    public Optional<TradeReputation> getByProfileId(String profileId) {
        return repository.findByProfileId(profileId);
    }

    /**
     * Processes a completed service execution from the upstream ServiceExecution
     * bounded context. This is the main business flow for the reputation subdomain.
     *
     * @param event the service execution completed integration event
     */
    public void processServiceCompletion(ServiceExecutionCompleted event) {
        TradeReputation reputation = createIfNotExists(event.profileId());

        recordReviewFromExecution(reputation, event);
        updateComplianceFromExecution(reputation, event);
        recalculateScoreAndBadges(reputation);

        repository.save(reputation);
    }

    /**
     * Re-triggers reputation recalculation for an existing profile.
     * Useful when calculation rules change and scores need to be refreshed.
     *
     * @param profileId the professional profile identifier
     */
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
