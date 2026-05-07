package com.veritrabajo.backend.customer.domain.port;

import com.veritrabajo.backend.customer.domain.model.Customer;

import java.util.Optional;
import java.util.UUID;

/**
 * Repository port for the Customer aggregate root.
 */
public interface CustomerRepository {

    Optional<Customer> findById(UUID id);

    Optional<Customer> findByEmail(String email);

    Customer save(Customer customer);
}
