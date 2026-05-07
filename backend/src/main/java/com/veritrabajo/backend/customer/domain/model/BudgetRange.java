package com.veritrabajo.backend.customer.domain.model;

import java.math.BigDecimal;
import java.util.Objects;

/**
 * Value object representing the preferred budget range of a customer.
 */
public final class BudgetRange {

    private final BigDecimal minimum;
    private final BigDecimal maximum;

    private BudgetRange(BigDecimal minimum, BigDecimal maximum) {
        this.minimum = minimum;
        this.maximum = maximum;
    }

    public static BudgetRange of(BigDecimal minimum, BigDecimal maximum) {
        Objects.requireNonNull(minimum, "Minimum budget is required");
        Objects.requireNonNull(maximum, "Maximum budget is required");
        if (minimum.signum() < 0 || maximum.signum() < 0) {
            throw new IllegalArgumentException("Budget values cannot be negative");
        }
        if (minimum.compareTo(maximum) > 0) {
            throw new IllegalArgumentException(
                    "Minimum budget cannot exceed maximum budget");
        }
        return new BudgetRange(minimum, maximum);
    }

    public BigDecimal minimum() {
        return minimum;
    }

    public BigDecimal maximum() {
        return maximum;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof BudgetRange that)) {
            return false;
        }
        return minimum.compareTo(that.minimum) == 0
                && maximum.compareTo(that.maximum) == 0;
    }

    @Override
    public int hashCode() {
        return Objects.hash(minimum, maximum);
    }
}
