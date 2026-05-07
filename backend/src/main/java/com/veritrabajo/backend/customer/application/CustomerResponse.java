package com.veritrabajo.backend.customer.application;

import com.veritrabajo.backend.customer.domain.model.Customer;
import com.veritrabajo.backend.customer.domain.model.SavedAddress;

import java.math.BigDecimal;
import java.time.Instant;
import java.util.List;
import java.util.UUID;

public record CustomerResponse(
        UUID id,
        String name,
        String email,
        String phoneNumber,
        String status,
        Instant registrationDate,
        List<AddressResponse> addresses,
        List<String> interestCategories,
        BigDecimal preferredBudgetMin,
        BigDecimal preferredBudgetMax
) {

    public static CustomerResponse from(Customer customer) {
        List<AddressResponse> addresses = customer.addresses().stream()
                .map(AddressResponse::from)
                .toList();
        return new CustomerResponse(
                customer.id(),
                customer.name(),
                customer.contactInfo().email(),
                customer.contactInfo().phoneNumber(),
                customer.status().name(),
                customer.registrationDate(),
                addresses,
                customer.preferences().interestCategories(),
                customer.preferences().preferredBudget().minimum(),
                customer.preferences().preferredBudget().maximum()
        );
    }

    public record AddressResponse(
            UUID id,
            String label,
            double latitude,
            double longitude,
            String description
    ) {
        public static AddressResponse from(SavedAddress address) {
            return new AddressResponse(
                    address.id(),
                    address.label(),
                    address.location().latitude(),
                    address.location().longitude(),
                    address.location().description()
            );
        }
    }
}
