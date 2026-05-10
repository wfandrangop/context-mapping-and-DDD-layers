package com.veritrabajo.backend.reputation.domain.model;

import java.util.Objects;

/** Score range: 0-100. */
public final class ConfidenceScore {

    public static final int MINIMUM = 0;
    public static final int MAXIMUM = 100;
    public static final int BASE = 0;

    private final int value;

    private ConfidenceScore(int value) {
        this.value = value;
    }

    public static ConfidenceScore base() {
        return new ConfidenceScore(BASE);
    }

    public static ConfidenceScore of(int value) {
        if (value < MINIMUM || value > MAXIMUM) {
            throw new IllegalArgumentException(String.format("Confidence score must be between %d" +
                    " and %d, got %d", MINIMUM, MAXIMUM, value));
        }
        return new ConfidenceScore(value);
    }

    public ConfidenceScore increment(int delta) {
        if (delta < 0) {
            throw new IllegalArgumentException("Increment cannot be negative");
        }
        return of(normalize(value + delta));
    }

    public ConfidenceScore decrement(int delta) {
        if (delta < 0) {
            throw new IllegalArgumentException("Decrement cannot be negative");
        }
        return of(normalize(value - delta));
    }

    public int value() {
        return value;
    }

    private static int normalize(int value) {
        return Math.max(MINIMUM, Math.min(MAXIMUM, value));
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof ConfidenceScore that)) {
            return false;
        }
        return value == that.value;
    }

    @Override
    public int hashCode() {
        return Objects.hash(value);
    }

    @Override
    public String toString() {
        return "ConfidenceScore{value=" + value + "}";
    }
}
