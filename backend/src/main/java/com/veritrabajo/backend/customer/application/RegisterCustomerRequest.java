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
        String phoneNumber
) {
    private static final BigDecimal DEFAULT_BUDGET = BigDecimal.ZERO;

    public CustomerCreation toCreation() {
        ContactInfo contact = ContactInfo.of(email, phoneNumber);
        ClientPreferences prefs = ClientPreferences.of(
                List.of(),
                BudgetRange.of(DEFAULT_BUDGET, DEFAULT_BUDGET)
        );
        return new CustomerCreation(name, contact, prefs);
    }
}
