package com.veritrabajo.backend.reputation.infrastructure.persistence;

import com.veritrabajo.backend.reputation.domain.model.ComplianceMetrics;
import com.veritrabajo.backend.reputation.domain.model.ConfidenceScore;
import com.veritrabajo.backend.reputation.domain.model.Review;
import com.veritrabajo.backend.reputation.domain.model.TradeReputation;
import com.veritrabajo.backend.reputation.domain.model.TradeReputationData;
import com.veritrabajo.backend.reputation.infrastructure.persistence.entity.ReviewEntity;
import com.veritrabajo.backend.reputation.infrastructure.persistence.entity.TradeReputationEntity;

import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;

public final class TradeReputationMapper {

    private TradeReputationMapper() {
    }

    public static TradeReputation toDomain(
            TradeReputationEntity entity) {
        List<Review> reviews = mapReviewsToDomain(entity);
        TradeReputationData data = buildDomainData(
                entity, reviews);
        return TradeReputation.rehydrate(data);
    }

    public static void updateEntity(
            TradeReputationEntity target,
            TradeReputation source) {
        target.setId(source.id());
        target.setProfileId(source.profileId());
        mapScalarFields(target, source);
        mapCollections(target, source);
    }

    private static TradeReputationData buildDomainData(
            TradeReputationEntity entity,
            List<Review> reviews) {
        return new TradeReputationData(
                entity.getId(),
                entity.getProfileId(),
                ConfidenceScore.of(entity.getConfidenceScore()),
                Set.copyOf(entity.getBadges()),
                ComplianceMetrics.of(
                        entity.getSuccessfulJobs(),
                        entity.getCancelledJobs()),
                reviews
        );
    }

    private static void mapScalarFields(
            TradeReputationEntity target,
            TradeReputation source) {
        int score = source.confidenceScore().value();
        target.setConfidenceScore(score);
        int successful = source.complianceMetrics()
                .successfulJobs();
        target.setSuccessfulJobs(successful);
        int cancelled = source.complianceMetrics()
                .cancelledJobs();
        target.setCancelledJobs(cancelled);
    }

    private static void mapCollections(
            TradeReputationEntity target,
            TradeReputation source) {
        target.setBadges(
                new LinkedHashSet<>(source.badges()));
        target.getReviews().clear();
        target.getReviews().addAll(
                mapReviewsToEntity(source.reviews()));
    }

    private static List<Review> mapReviewsToDomain(
            TradeReputationEntity entity) {
        return entity.getReviews().stream()
                .map(TradeReputationMapper::toDomainReview)
                .toList();
    }

    private static Review toDomainReview(ReviewEntity re) {
        return Review.rehydrate(
                re.getId(), re.getComment(), re.getRating());
    }

    private static List<ReviewEntity> mapReviewsToEntity(
            List<Review> reviews) {
        return reviews.stream()
                .map(TradeReputationMapper::toEntityReview)
                .toList();
    }

    private static ReviewEntity toEntityReview(Review rev) {
        ReviewEntity entity = new ReviewEntity();
        entity.setId(rev.id());
        entity.setComment(rev.comment());
        entity.setRating(rev.rating());
        return entity;
    }
}
