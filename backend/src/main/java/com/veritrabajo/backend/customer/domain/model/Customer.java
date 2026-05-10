package com.veritrabajo.backend.customer.domain.model;

import com.veritrabajo.backend.customer.domain.event.CustomerRegistered;
import com.veritrabajo.backend.customer.domain.event.ServiceRequestedByCustomer;

import java.time.Instant;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;

public final class Customer {

    private final CustomerId id;
    private final AuthUserId authUserId;
    private final String name;
    private final Instant registrationDate;
    private CustomerStatus status;
    private ContactInfo contactInfo;
    private final List<SavedAddress> addresses;
    private ClientPreferences preferences;

    private Customer(CustomerData data) {
        this.id = Objects.requireNonNull(data.id(), "Customer id is required");
        this.authUserId = Objects.requireNonNull(data.authUserId(), "authUserId is required");
        this.name = validateName(data.name());
        this.registrationDate = Objects.requireNonNull(data.registrationDate(),
                "Registration date is required");
        this.status = Objects.requireNonNull(data.status(), "Status is required");
        this.contactInfo = Objects.requireNonNull(data.contactInfo(),
                "Contact info is required");
        this.addresses = new ArrayList<>(Objects.requireNonNull(data.addresses(),
                "Addresses list is required"));
        this.preferences = Objects.requireNonNull(data.preferences(),
                "Preferences are required");
    }

    public static Customer create(AuthUserId authUserId, CustomerCreation creation) {
        Objects.requireNonNull(authUserId, "authUserId is required");
        Objects.requireNonNull(creation, "Customer creation is required");
        return new Customer(new CustomerData(
                CustomerId.generate(),
                authUserId,
                creation.name(),
                Instant.now(),
                CustomerStatus.ACTIVE,
                creation.contactInfo(),
                List.of(),
                creation.preferences()
        ));
    }

    public static Customer rehydrate(CustomerData data) {
        Objects.requireNonNull(data, "Customer data is required");
        return new Customer(data);
    }

    public CustomerId id() {
        return id;
    }

    public AuthUserId authUserId() {
        return authUserId;
    }

    public String name() {
        return name;
    }

    public Instant registrationDate() {
        return registrationDate;
    }

    public CustomerStatus status() {
        return status;
    }

    public ContactInfo contactInfo() {
        return contactInfo;
    }

    public List<SavedAddress> addresses() {
        return List.copyOf(addresses);
    }

    public ClientPreferences preferences() {
        return preferences;
    }

    public boolean isActive() {
        return status == CustomerStatus.ACTIVE;
    }

    public CustomerRegistered registered() {
        return new CustomerRegistered(id.value(), contactInfo.email());
    }

    /**
     * Adds a new saved address (Home, Office, etc.). Returns the created address
     * so the caller can reference its generated id.
     */
    public SavedAddress addAddress(String label, Location location) {
        SavedAddress address = SavedAddress.create(label, location);
        addresses.add(address);
        return address;
    }

    public void updatePreferences(ClientPreferences newPreferences) {
        this.preferences = Objects.requireNonNull(newPreferences,
                "Preferences are required");
    }

    public void updateContactInfo(ContactInfo newContactInfo) {
        this.contactInfo = Objects.requireNonNull(newContactInfo,
                "Contact info is required");
    }

    public ServiceRequestedByCustomer requestService(UUID jobPostId, UUID addressId) {
        Objects.requireNonNull(jobPostId, "Job post id is required");
        Objects.requireNonNull(addressId, "Address id is required");
        validateActive();
        validateOwnsAddress(addressId);
        return new ServiceRequestedByCustomer(id.value(), jobPostId, addressId);
    }

    public void ban() {
        this.status = CustomerStatus.BANNED;
    }

    public void reactivate() {
        this.status = CustomerStatus.ACTIVE;
    }

    private void validateActive() {
        if (status != CustomerStatus.ACTIVE) {
            throw new IllegalStateException(
                    "Banned customers cannot request services");
        }
    }

    private void validateOwnsAddress(UUID addressId) {
        Optional<SavedAddress> match = addresses.stream()
                .filter(address -> address.id().equals(addressId))
                .findFirst();
        if (match.isEmpty()) {
            throw new IllegalArgumentException(
                    "Address " + addressId + " does not belong to this customer");
        }
    }

    private static String validateName(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Customer name is required");
        }
        return name.trim();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Customer that)) {
            return false;
        }
        return id.equals(that.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
