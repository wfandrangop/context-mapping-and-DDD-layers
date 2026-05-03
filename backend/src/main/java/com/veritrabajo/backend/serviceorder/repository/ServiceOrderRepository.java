package com.veritrabajo.backend.serviceorder.repository;

import com.veritrabajo.backend.serviceorder.domain.model.ServiceOrder;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

/**
 * Repository port for the ServiceOrder aggregate root.
 */
public interface ServiceOrderRepository {

    Optional<ServiceOrder> findById(UUID id);

    ServiceOrder save(ServiceOrder order);

    List<ServiceOrder> findByWorkerId(String workerId);
}
