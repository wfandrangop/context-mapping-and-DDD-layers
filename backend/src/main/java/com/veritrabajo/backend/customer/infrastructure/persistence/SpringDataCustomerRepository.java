package com.veritrabajo.backend.customer.infrastructure.persistence;

import com.veritrabajo.backend.customer.infrastructure.persistence.entity.CustomerEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

/**
 * Spring Data repository for CustomerEntity persistence operations.
 */
public interface SpringDataCustomerRepository extends JpaRepository<CustomerEntity, UUID> {

    Optional<CustomerEntity> findByEmail(String email);
}
