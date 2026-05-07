package com.veritrabajo.backend.customer.application;

import com.veritrabajo.backend.customer.domain.event.ServiceRequestedByCustomer;
import com.veritrabajo.backend.customer.domain.exception.CustomerNotFoundException;
import com.veritrabajo.backend.customer.domain.model.ClientPreferences;
import com.veritrabajo.backend.customer.domain.model.Customer;
import com.veritrabajo.backend.customer.domain.model.CustomerCreation;
import com.veritrabajo.backend.customer.domain.model.Location;
import com.veritrabajo.backend.customer.domain.model.SavedAddress;
import com.veritrabajo.backend.customer.domain.port.CustomerRepository;
import com.veritrabajo.backend.customer.domain.port.DomainEventPublisher;
import com.veritrabajo.backend.customer.domain.service.ClientValidationService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.util.UUID;

@Service
public class CustomerApplicationService {

    private final CustomerRepository repository;
    private final DomainEventPublisher eventPublisher;
    private final ClientValidationService validationService;

    public CustomerApplicationService(
            CustomerRepository repository,
            DomainEventPublisher eventPublisher
    ) {
        this.repository = repository;
        this.eventPublisher = eventPublisher;
        this.validationService = new ClientValidationService();
    }

    @Transactional
    public Customer register(CustomerCreation creation) {
        repository.findByEmail(creation.contactInfo().email())
                .ifPresent(existing -> {
                    throw new IllegalArgumentException(
                            "Email already registered: " + creation.contactInfo().email());
                });
        Customer saved = repository.save(Customer.create(creation));
        eventPublisher.publish(saved.registered());
        return saved;
    }

    @Transactional
    public Customer updatePreferences(UUID customerId, ClientPreferences preferences) {
        Customer customer = findOrThrow(customerId);
        customer.updatePreferences(preferences);
        return repository.save(customer);
    }

    @Transactional
    public SavedAddress addAddress(UUID customerId, String label, Location location) {
        Customer customer = findOrThrow(customerId);
        SavedAddress added = customer.addAddress(label, location);
        repository.save(customer);
        return added;
    }

    @Transactional
    public void requestService(UUID customerId, UUID jobPostId, UUID addressId) {
        Customer customer = findOrThrow(customerId);
        validationService.ensureEligibleForNewService(customer, 0, BigDecimal.ZERO);
        ServiceRequestedByCustomer event = customer.requestService(jobPostId, addressId);
        eventPublisher.publish(event);
    }

    public Customer getById(UUID customerId) {
        return findOrThrow(customerId);
    }

    private Customer findOrThrow(UUID customerId) {
        return repository.findById(customerId)
                .orElseThrow(() -> new CustomerNotFoundException(customerId));
    }
}
