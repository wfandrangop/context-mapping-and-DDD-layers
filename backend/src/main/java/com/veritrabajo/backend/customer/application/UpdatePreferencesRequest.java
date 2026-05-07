package com.veritrabajo.backend.customer.application;

import com.veritrabajo.backend.customer.domain.model.BudgetRange;
import com.veritrabajo.backend.customer.domain.model.ClientPreferences;

import java.math.BigDecimal;
import java.util.List;

public record UpdatePreferencesRequest(
        List<String> interestCategories,
        BigDecimal preferredBudgetMin,
        BigDecimal preferredBudgetMax
) {
    public ClientPreferences toPreferences() {
        return ClientPreferences.of(
                interestCategories,
                BudgetRange.of(preferredBudgetMin, preferredBudgetMax)
        );
    }
}
