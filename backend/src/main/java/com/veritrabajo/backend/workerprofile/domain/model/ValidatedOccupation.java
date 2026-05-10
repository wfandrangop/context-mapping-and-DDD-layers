package com.veritrabajo.backend.workerprofile.domain.model;

import jakarta.persistence.Embeddable;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;

/**
 * Value Object — ValidatedOccupation
 *
 * Represents the worker's main trade validated and assigned by the AI,
 * along with the expertise level calculated from their description.
 *
 * Examples of occupation name: "Electrician", "Plumber", "Bricklayer".
 *
 * It is @Embeddable because JPA stores its fields as columns
 * inside the worker_profile table — not as a separate table.
 *
 * Expertise levels:
 *  BEGINNER     → 0 to 2 years of experience
 *  INTERMEDIATE → 3 to 7 years of experience
 *  EXPERT       → more than 7 years of experience
 */
@Embeddable
public class ValidatedOccupation {

    public enum ExpertiseLevel {
        BEGINNER,
        INTERMEDIATE,
        EXPERT
    }

    private String occupationName;

    @Enumerated(EnumType.STRING)
    private ExpertiseLevel expertiseLevel;

    /**
     * Required by JPA. Do not use in business logic.
     */
    protected ValidatedOccupation() {}

    public ValidatedOccupation(String occupationName, ExpertiseLevel expertiseLevel) {
        if (occupationName == null || occupationName.isBlank()) {
            throw new IllegalArgumentException(
                    "Occupation name cannot be empty."
            );
        }
        this.occupationName = occupationName.trim();
        this.expertiseLevel = expertiseLevel != null ? expertiseLevel : ExpertiseLevel.BEGINNER;
    }

    public String getOccupationName() {
        return occupationName;
    }

    public ExpertiseLevel getExpertiseLevel() {
        return expertiseLevel;
    }
}
