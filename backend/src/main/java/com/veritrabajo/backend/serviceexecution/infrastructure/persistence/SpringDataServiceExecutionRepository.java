package com.veritrabajo.backend.serviceexecution.infrastructure.persistence;

import com.veritrabajo.backend.serviceexecution.infrastructure.persistence.entity.ServiceExecutionEntity;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.UUID;

/**
 * Spring Data repository for service execution entities.
 */
public interface SpringDataServiceExecutionRepository
        extends JpaRepository<ServiceExecutionEntity, UUID> {

    List<ServiceExecutionEntity> findByWorkerId(String workerId);
}
