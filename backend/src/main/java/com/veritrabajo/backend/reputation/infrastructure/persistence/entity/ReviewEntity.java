package com.veritrabajo.backend.reputation.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

import java.util.UUID;

/**
 * JPA entity mapping a review to the {@code reviews} table.
 * This is an infrastructure data-carrier; domain invariants
 * are enforced by the domain model.
 */
@Entity
@Table(name = "reviews")
public class ReviewEntity {

    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(nullable = false, length = 2000)
    private String comment;

    @Column(nullable = false)
    private int rating;

    public ReviewEntity() {
        // Required by JPA
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getComment() {
        return comment;
    }

    public void setComment(String comment) {
        this.comment = comment;
    }

    public int getRating() {
        return rating;
    }

    public void setRating(int rating) {
        this.rating = rating;
    }
}
