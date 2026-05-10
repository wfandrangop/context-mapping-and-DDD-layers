package com.veritrabajo.backend.jobmarketplace.domain.model;

import java.math.BigDecimal;
import java.util.Objects;

public record EstimatedBudget(BigDecimal minimum, BigDecimal maximum) {

    private static final BigDecimal ZERO = BigDecimal.ZERO;

    public EstimatedBudget {
        Objects.requireNonNull(minimum, "Minimum budget is required");
        Objects.requireNonNull(maximum, "Maximum budget is required");
        validateNonNegative(minimum, "Minimum budget cannot be negative");
        validateNonNegative(maximum, "Maximum budget cannot be negative");
        validateRange(minimum, maximum);
    }

    private static void validateNonNegative(BigDecimal value, String message) {
        if (value.compareTo(ZERO) < 0) {
            throw new IllegalArgumentException(message);
        }
    }

    private static void validateRange(BigDecimal minimum, BigDecimal maximum) {
        if (maximum.compareTo(minimum) < 0) {
            throw new IllegalArgumentException("Maximum budget cannot be lower than minimum");
        }
    }
}
