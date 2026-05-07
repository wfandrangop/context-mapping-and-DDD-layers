package com.veritrabajo.backend.customer.infrastructure.persistence;

import com.veritrabajo.backend.customer.domain.model.Customer;
import com.veritrabajo.backend.customer.domain.port.CustomerRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory repository implementation for the in-memory profile.
 */
@Repository
@Profile("in-memory")
public class InMemoryCustomerRepository implements CustomerRepository {

    private final Map<UUID, Customer> storeById = new ConcurrentHashMap<>();

    @Override
    public Optional<Customer> findById(UUID id) {
        return Optional.ofNullable(storeById.get(id));
    }

    @Override
    public Optional<Customer> findByEmail(String email) {
        if (email == null) {
            return Optional.empty();
        }
        String normalized = email.trim().toLowerCase();
        return storeById.values()
                .stream()
                .filter(customer -> customer.contactInfo().email().equals(normalized))
                .findFirst();
    }

    @Override
    public Customer save(Customer customer) {
        storeById.put(customer.id(), customer);
        return customer;
    }
}
