package com.veritrabajo.backend.customer.application;

import com.veritrabajo.backend.customer.domain.model.BudgetRange;
import com.veritrabajo.backend.customer.domain.model.ClientPreferences;
import com.veritrabajo.backend.customer.domain.model.ContactInfo;
import com.veritrabajo.backend.customer.domain.model.CustomerCreation;

import java.math.BigDecimal;
import java.util.List;

public record RegisterCustomerRequest(
        String name,
        String email,
        String phoneNumber,
        List<String> interestCategories,
        BigDecimal preferredBudgetMin,
        BigDecimal preferredBudgetMax
) {
    public CustomerCreation toCreation() {
        ContactInfo contact = ContactInfo.of(email, phoneNumber);
        ClientPreferences prefs = ClientPreferences.of(
                interestCategories,
                BudgetRange.of(preferredBudgetMin, preferredBudgetMax)
        );
        return new CustomerCreation(name, contact, prefs);
    }
}
