package com.veritrabajo.backend.customer.domain.model;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

/**
 * Value object grouping the customer's interest categories and preferred budget.
 */
public final class ClientPreferences {

    private final List<String> interestCategories;
    private final BudgetRange preferredBudget;

    private ClientPreferences(List<String> interestCategories, BudgetRange preferredBudget) {
        this.interestCategories = List.copyOf(interestCategories);
        this.preferredBudget = preferredBudget;
    }

    public static ClientPreferences of(Collection<String> categories, BudgetRange budget) {
        Objects.requireNonNull(categories, "Interest categories are required");
        Objects.requireNonNull(budget, "Preferred budget is required");
        Set<String> normalized = normalize(categories);
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException(
                    "At least one interest category is required");
        }
        return new ClientPreferences(List.copyOf(normalized), budget);
    }

    public List<String> interestCategories() {
        return interestCategories;
    }

    public BudgetRange preferredBudget() {
        return preferredBudget;
    }

    private static Set<String> normalize(Collection<String> categories) {
        Set<String> normalized = new LinkedHashSet<>();
        for (String category : categories) {
            if (category != null && !category.isBlank()) {
                normalized.add(category.trim());
            }
        }
        return normalized;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof ClientPreferences that)) {
            return false;
        }
        return interestCategories.equals(that.interestCategories)
                && preferredBudget.equals(that.preferredBudget);
    }

    @Override
    public int hashCode() {
        return Objects.hash(interestCategories, preferredBudget);
    }
}
