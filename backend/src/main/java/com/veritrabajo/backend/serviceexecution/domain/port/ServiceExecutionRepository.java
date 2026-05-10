package com.veritrabajo.backend.serviceexecution.domain.port;

import com.veritrabajo.backend.serviceexecution.domain.model.ServiceExecution;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

public interface ServiceExecutionRepository {

    Optional<ServiceExecution> findById(UUID id);

    ServiceExecution save(ServiceExecution execution);

    List<ServiceExecution> findByWorkerId(String workerId);
}
