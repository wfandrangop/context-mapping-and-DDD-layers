package com.veritrabajo.backend.customer.infrastructure.persistence;

import com.veritrabajo.backend.customer.domain.model.BudgetRange;
import com.veritrabajo.backend.customer.domain.model.ClientPreferences;
import com.veritrabajo.backend.customer.domain.model.ContactInfo;
import com.veritrabajo.backend.customer.domain.model.Customer;
import com.veritrabajo.backend.customer.domain.model.CustomerData;
import com.veritrabajo.backend.customer.domain.model.CustomerStatus;
import com.veritrabajo.backend.customer.domain.model.Location;
import com.veritrabajo.backend.customer.domain.model.SavedAddress;
import com.veritrabajo.backend.customer.infrastructure.persistence.entity.CustomerEntity;
import com.veritrabajo.backend.customer.infrastructure.persistence.entity.SavedAddressEmbeddable;

import java.util.ArrayList;
import java.util.List;

/**
 * Mapper for translating between Customer aggregate and its JPA entity.
 */
public final class CustomerMapper {

    private CustomerMapper() {
        // Utility class
    }

    public static Customer toDomain(CustomerEntity entity) {
        CustomerData data = new CustomerData(
                entity.getId(),
                entity.getName(),
                entity.getRegistrationDate(),
                CustomerStatus.valueOf(entity.getStatus()),
                ContactInfo.of(entity.getEmail(), entity.getPhoneNumber()),
                toDomainAddresses(entity.getAddresses()),
                ClientPreferences.of(
                        entity.getInterestCategories(),
                        BudgetRange.of(
                                entity.getPreferredBudgetMin(),
                                entity.getPreferredBudgetMax())
                )
        );
        return Customer.rehydrate(data);
    }

    public static void updateEntity(CustomerEntity target, Customer source) {
        target.setId(source.id());
        target.setName(source.name());
        target.setEmail(source.contactInfo().email());
        target.setPhoneNumber(source.contactInfo().phoneNumber());
        target.setStatus(source.status().name());
        target.setRegistrationDate(source.registrationDate());
        target.setInterestCategories(
                new ArrayList<>(source.preferences().interestCategories()));
        target.setPreferredBudgetMin(source.preferences().preferredBudget().minimum());
        target.setPreferredBudgetMax(source.preferences().preferredBudget().maximum());
        target.setAddresses(toEmbeddables(source.addresses()));
    }

    private static List<SavedAddress> toDomainAddresses(
            List<SavedAddressEmbeddable> source
    ) {
        return new ArrayList<>(source.stream()
                .map(CustomerMapper::toDomainAddress)
                .toList());
    }

    private static SavedAddress toDomainAddress(SavedAddressEmbeddable source) {
        Location location = Location.of(
                source.getLatitude(),
                source.getLongitude(),
                source.getDescription()
        );
        return SavedAddress.rehydrate(source.getAddressId(), source.getLabel(), location);
    }

    private static List<SavedAddressEmbeddable> toEmbeddables(List<SavedAddress> source) {
        return new ArrayList<>(source.stream()
                .map(CustomerMapper::toEmbeddable)
                .toList());
    }

    private static SavedAddressEmbeddable toEmbeddable(SavedAddress source) {
        SavedAddressEmbeddable embeddable = new SavedAddressEmbeddable();
        embeddable.setAddressId(source.id());
        embeddable.setLabel(source.label());
        embeddable.setLatitude(source.location().latitude());
        embeddable.setLongitude(source.location().longitude());
        embeddable.setDescription(source.location().description());
        return embeddable;
    }
}
