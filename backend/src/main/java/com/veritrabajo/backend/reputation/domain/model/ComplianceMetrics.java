package com.veritrabajo.backend.reputation.domain.model;

import java.util.Objects;

/**
 * Value Object representing compliance metrics for a trade professional.
 * Tracks successful and cancelled jobs.
 */
public final class ComplianceMetrics {

    private final int successfulJobs;
    private final int cancelledJobs;
    private static final double PERCENTAGE_SCALE = 100.0;

    private ComplianceMetrics(int successfulJobs, int cancelledJobs) {
        this.successfulJobs = successfulJobs;
        this.cancelledJobs = cancelledJobs;
    }

    public static ComplianceMetrics empty() {
        return new ComplianceMetrics(0, 0);
    }

    public static ComplianceMetrics of(int successfulJobs, int cancelledJobs) {
        if (successfulJobs < 0 || cancelledJobs < 0) {
            throw new IllegalArgumentException("Metrics cannot have negative values");
        }
        return new ComplianceMetrics(successfulJobs, cancelledJobs);
    }

    public int successfulJobs() {
        return successfulJobs;
    }

    public int cancelledJobs() {
        return cancelledJobs;
    }

    public int totalJobs() {
        return successfulJobs + cancelledJobs;
    }

    public double successPercentage() {
        int total = totalJobs();
        if (total == 0) {
            return 0.0;
        }
        return (successfulJobs * PERCENTAGE_SCALE) / total;
    }

    /**
     * Returns a new ComplianceMetrics with one additional successful job recorded.
     *
     * @return new metrics instance with incremented successful jobs count
     */
    public ComplianceMetrics withSuccessfulJob() {
        return new ComplianceMetrics(successfulJobs + 1, cancelledJobs);
    }

    /**
     * Returns a new ComplianceMetrics with one additional cancelled job recorded.
     *
     * @return new metrics instance with incremented cancelled jobs count
     */
    public ComplianceMetrics withCancelledJob() {
        return new ComplianceMetrics(successfulJobs, cancelledJobs + 1);
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof ComplianceMetrics that)) {
            return false;
        }
        return successfulJobs == that.successfulJobs && cancelledJobs == that.cancelledJobs;
    }

    @Override
    public int hashCode() {
        return Objects.hash(successfulJobs, cancelledJobs);
    }

    @Override
    public String toString() {
        return "ComplianceMetrics{successful=" + successfulJobs + ", cancelled=" + cancelledJobs
                + "}";
    }
}
