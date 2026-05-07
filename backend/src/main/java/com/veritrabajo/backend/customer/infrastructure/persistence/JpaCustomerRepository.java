package com.veritrabajo.backend.customer.infrastructure.persistence;

import com.veritrabajo.backend.customer.domain.model.Customer;
import com.veritrabajo.backend.customer.domain.port.CustomerRepository;
import com.veritrabajo.backend.customer.infrastructure.persistence.entity.CustomerEntity;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

/**
 * JPA-backed repository implementation for Customer aggregate persistence.
 */
@Repository
@Profile("!in-memory")
public class JpaCustomerRepository implements CustomerRepository {

    private final SpringDataCustomerRepository springDataRepository;

    public JpaCustomerRepository(SpringDataCustomerRepository springDataRepository) {
        this.springDataRepository = springDataRepository;
    }

    @Override
    public Optional<Customer> findById(UUID id) {
        return springDataRepository.findById(id)
                .map(CustomerMapper::toDomain);
    }

    @Override
    public Optional<Customer> findByEmail(String email) {
        return springDataRepository.findByEmail(email)
                .map(CustomerMapper::toDomain);
    }

    @Override
    public Customer save(Customer customer) {
        CustomerEntity entity = springDataRepository.findById(customer.id())
                .orElseGet(CustomerEntity::new);
        CustomerMapper.updateEntity(entity, customer);
        springDataRepository.save(entity);
        return customer;
    }
}
