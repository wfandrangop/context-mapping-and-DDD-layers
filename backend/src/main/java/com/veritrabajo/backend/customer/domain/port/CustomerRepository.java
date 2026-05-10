package com.veritrabajo.backend.customer.domain.port;

import com.veritrabajo.backend.customer.domain.model.AuthUserId;
import com.veritrabajo.backend.customer.domain.model.Customer;
import com.veritrabajo.backend.customer.domain.model.CustomerId;

import java.util.Optional;

public interface CustomerRepository {

    Optional<Customer> findById(CustomerId id);

    Optional<Customer> findByEmail(String email);

    Optional<Customer> findByAuthUserId(AuthUserId authUserId);

    boolean existsByAuthUserId(AuthUserId authUserId);

    Customer save(Customer customer);
}
