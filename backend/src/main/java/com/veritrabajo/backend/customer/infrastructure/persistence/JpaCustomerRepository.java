package com.veritrabajo.backend.customer.infrastructure.persistence;

import com.veritrabajo.backend.customer.domain.model.AuthUserId;
import com.veritrabajo.backend.customer.domain.model.Customer;
import com.veritrabajo.backend.customer.domain.model.CustomerId;
import com.veritrabajo.backend.customer.domain.port.CustomerRepository;
import com.veritrabajo.backend.customer.infrastructure.persistence.entity.CustomerEntity;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public class JpaCustomerRepository implements CustomerRepository {

    private final SpringDataCustomerRepository springDataRepository;

    public JpaCustomerRepository(SpringDataCustomerRepository springDataRepository) {
        this.springDataRepository = springDataRepository;
    }

    @Override
    public Optional<Customer> findById(CustomerId id) {
        return springDataRepository.findById(id.value())
                .map(CustomerMapper::toDomain);
    }

    @Override
    public Optional<Customer> findByEmail(String email) {
        return springDataRepository.findByEmail(email)
                .map(CustomerMapper::toDomain);
    }

    @Override
    public Optional<Customer> findByAuthUserId(AuthUserId authUserId) {
        return springDataRepository.findByAuthUserId(authUserId.value())
                .map(CustomerMapper::toDomain);
    }

    @Override
    public boolean existsByAuthUserId(AuthUserId authUserId) {
        return springDataRepository.existsByAuthUserId(authUserId.value());
    }

    @Override
    public Customer save(Customer customer) {
        CustomerEntity entity = springDataRepository.findById(customer.id().value())
                .orElseGet(CustomerEntity::new);
        CustomerMapper.updateEntity(entity, customer);
        springDataRepository.save(entity);
        return customer;
    }
}
