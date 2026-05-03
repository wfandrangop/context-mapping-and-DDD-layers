package com.veritrabajo.backend.reputation.domain.service;

import com.veritrabajo.backend.reputation.domain.model.Badge;
import com.veritrabajo.backend.reputation.domain.model.ComplianceMetrics;
import com.veritrabajo.backend.reputation.domain.model.ConfidenceScore;
import com.veritrabajo.backend.reputation.domain.model.Review;
import com.veritrabajo.backend.reputation.domain.model.TradeReputation;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 * Domain service for calculating reputation metrics.
 * Encapsulates business rules that span across multiple value objects
 * within the {@link TradeReputation} aggregate.
 * <p>
 * Score formula: 60% average review rating (normalized to 0-100) + 40% compliance success rate.
 * <p>
 * Badge thresholds:
 * <ul>
 *   <li>VERIFIED — at least 1 successful job completed</li>
 *   <li>SILVER — score >= 60, at least 5 reviews</li>
 *   <li>GOLD — score >= 85, at least 10 reviews, compliance >= 90%</li>
 * </ul>
 */
public class ReputationCalculator {

    private static final double REVIEW_WEIGHT = 0.6;
    private static final double COMPLIANCE_WEIGHT = 0.4;
    private static final int MAX_RATING = 5;
    private static final int PERCENTAGE_SCALE = 100;

    private static final int SILVER_SCORE_THRESHOLD = 60;
    private static final int SILVER_MIN_REVIEWS = 5;
    private static final int GOLD_SCORE_THRESHOLD = 85;
    private static final int GOLD_MIN_REVIEWS = 10;
    private static final double GOLD_MIN_COMPLIANCE = 90.0;
    private static final int VERIFIED_MIN_SUCCESSFUL_JOBS = 1;

    /**
     * Calculates the confidence score based on the current state of the reputation.
     *
     * @param reputation the trade reputation aggregate
     * @return the computed confidence score
     */
    public ConfidenceScore calculateScore(TradeReputation reputation) {
        double reviewComponent = calculateReviewComponent(reputation.reviews());
        double complianceComponent = reputation.complianceMetrics().successPercentage();

        int weightedScore = (int) Math.round(
                (reviewComponent * REVIEW_WEIGHT) + (complianceComponent * COMPLIANCE_WEIGHT)
        );

        return ConfidenceScore.of(weightedScore);
    }

    /**
     * Determines which badges should be awarded based on the current reputation state.
     *
     * @param reputation the trade reputation aggregate
     * @return the set of badges the professional qualifies for
     */
    public Set<Badge> calculateBadges(TradeReputation reputation) {
        Set<Badge> earnedBadges = new HashSet<>();

        evaluateVerifiedBadge(reputation.complianceMetrics(), earnedBadges);
        evaluateSilverBadge(reputation, earnedBadges);
        evaluateGoldBadge(reputation, earnedBadges);

        return Set.copyOf(earnedBadges);
    }

    private void evaluateVerifiedBadge(ComplianceMetrics metrics,
                                       Set<Badge> badges) {
        if (metrics.successfulJobs() >= VERIFIED_MIN_SUCCESSFUL_JOBS) {
            badges.add(Badge.VERIFIED);
        }
    }

    private void evaluateSilverBadge(TradeReputation reputation,
                                     Set<Badge> badges) {
        boolean meetsScore = reputation.confidenceScore().value()
                >= SILVER_SCORE_THRESHOLD;
        boolean meetsReviews = reputation.reviews().size()
                >= SILVER_MIN_REVIEWS;

        if (meetsScore && meetsReviews) {
            badges.add(Badge.SILVER);
        }
    }

    private void evaluateGoldBadge(TradeReputation reputation,
                                   Set<Badge> badges) {
        boolean meetsScore = reputation.confidenceScore().value()
                >= GOLD_SCORE_THRESHOLD;
        boolean meetsReviews = reputation.reviews().size()
                >= GOLD_MIN_REVIEWS;
        boolean meetsCompliance = reputation.complianceMetrics()
                .successPercentage() >= GOLD_MIN_COMPLIANCE;

        if (meetsScore && meetsReviews && meetsCompliance) {
            badges.add(Badge.GOLD);
        }
    }

    private double calculateReviewComponent(List<Review> reviews) {
        if (reviews.isEmpty()) {
            return 0.0;
        }

        double averageRating = reviews.stream()
                .mapToInt(Review::rating)
                .average()
                .orElse(0.0);

        return (averageRating / MAX_RATING) * PERCENTAGE_SCALE;
    }
}
