package com.veritrabajo.backend.reputation.domain.model;

import java.util.Objects;
import java.util.UUID;

/**
 * Entity representing a customer review for a trade professional.
 * Immutable once created.
 */
public final class Review {

    private static final int MINIMUM_RATING = 1;
    private static final int MAXIMUM_RATING = 5;

    private final UUID id;
    private final String comment;
    private final int rating;

    private Review(UUID id, String comment, int rating) {
        this.id = Objects.requireNonNull(id, "Review id is required");
        this.comment = validateComment(comment);
        this.rating = validateRating(rating);
    }

    public static Review create(String comment, int rating) {
        return new Review(UUID.randomUUID(), comment, rating);
    }

    public UUID id() {
        return id;
    }

    public String comment() {
        return comment;
    }

    public int rating() {
        return rating;
    }

    private static String validateComment(String comment) {
        if (comment == null || comment.isBlank()) {
            throw new IllegalArgumentException("Review comment is required");
        }
        return comment;
    }

    private static int validateRating(int rating) {
        if (rating < MINIMUM_RATING || rating > MAXIMUM_RATING) {
            throw new IllegalArgumentException(
                    String.format("Rating must be between %d and %d, got %d",
                            MINIMUM_RATING, MAXIMUM_RATING, rating));
        }
        return rating;
    }

    @Override
    public String toString() {
        return "Review{id=" + id + ", rating=" + rating + "}";
    }
}
