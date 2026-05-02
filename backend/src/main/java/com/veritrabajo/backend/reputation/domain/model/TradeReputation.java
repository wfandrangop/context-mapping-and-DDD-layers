package com.veritrabajo.backend.reputation.domain.model;

import com.veritrabajo.backend.reputation.event.BadgeAwarded;
import com.veritrabajo.backend.reputation.event.ReputationUpdated;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.Set;
import java.util.UUID;

/**
 * Aggregate Root representing the reputation of a trade professional.
 * Manages confidence score, badges, compliance metrics, and reviews.
 */
public final class TradeReputation {

    private final UUID id;
    private final String profileId;
    private ConfidenceScore confidenceScore;
    private final Set<Badge> badges;
    private ComplianceMetrics complianceMetrics;
    private final List<Review> reviews;

    private TradeReputation(
        UUID id,
        String profileId,
        ConfidenceScore confidenceScore,
        Set<Badge> badges,
        ComplianceMetrics complianceMetrics,
        List<Review> reviews
    ) {
        this.id = Objects.requireNonNull(id, "Reputation id is required");
        this.profileId = validateProfileId(profileId);
        this.confidenceScore = Objects.requireNonNull(confidenceScore, "Confidence score is required");
        this.badges = new LinkedHashSet<>(Objects.requireNonNull(badges, "Badges are required"));
        this.complianceMetrics = Objects.requireNonNull(complianceMetrics, "Compliance metrics are required");
        this.reviews = new ArrayList<>(Objects.requireNonNull(reviews, "Reviews are required"));
    }

    public static TradeReputation createInitial(String profileId) {
        return new TradeReputation(
            UUID.randomUUID(),
            profileId,
            ConfidenceScore.base(),
            Set.of(),
            ComplianceMetrics.empty(),
            List.of()
        );
    }

    public UUID id() {
        return id;
    }

    public String profileId() {
        return profileId;
    }

    public ConfidenceScore confidenceScore() {
        return confidenceScore;
    }

    public Set<Badge> badges() {
        return Set.copyOf(badges);
    }

    public ComplianceMetrics complianceMetrics() {
        return complianceMetrics;
    }

    public List<Review> reviews() {
        return List.copyOf(reviews);
    }

    public ReputationUpdated updateScore(ConfidenceScore newScore) {
        Objects.requireNonNull(newScore, "New score is required");

        ConfidenceScore previousScore = this.confidenceScore;
        this.confidenceScore = newScore;

        return new ReputationUpdated(id, profileId, previousScore, newScore);
    }

    public Optional<BadgeAwarded> awardBadge(Badge badge) {
        Objects.requireNonNull(badge, "Badge is required");

        if (!badges.add(badge)) {
            return Optional.empty();
        }

        return Optional.of(new BadgeAwarded(id, profileId, badge));
    }

    public void updateMetrics(ComplianceMetrics metrics) {
        this.complianceMetrics = Objects.requireNonNull(metrics, "Metrics are required");
    }

    public void recordReview(Review review) {
        reviews.add(Objects.requireNonNull(review, "Review is required"));
    }

    private static String validateProfileId(String profileId) {
        if (profileId == null || profileId.isBlank()) {
            throw new IllegalArgumentException("Profile id is required");
        }
        return profileId;
    }

    @Override
    public String toString() {
        return "TradeReputation{id=" + id + ", profileId='" + profileId + "', score=" + confidenceScore + "}";
    }
}

