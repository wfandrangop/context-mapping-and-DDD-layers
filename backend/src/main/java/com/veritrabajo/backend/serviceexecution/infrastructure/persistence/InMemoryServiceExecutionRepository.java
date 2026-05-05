package com.veritrabajo.backend.serviceexecution.infrastructure.persistence;

import com.veritrabajo.backend.serviceexecution.domain.model.ServiceExecution;
import com.veritrabajo.backend.serviceexecution.domain.port.ServiceExecutionRepository;
import org.springframework.context.annotation.Profile;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * In-memory repository implementation for service execution in the in-memory profile.
 */
@Repository
@Profile("in-memory")
public class InMemoryServiceExecutionRepository implements ServiceExecutionRepository {

    private final Map<UUID, ServiceExecution> storeById = new ConcurrentHashMap<>();

    @Override
    public Optional<ServiceExecution> findById(UUID id) {
        return Optional.ofNullable(storeById.get(id));
    }

    @Override
    public ServiceExecution save(ServiceExecution execution) {
        storeById.put(execution.id(), execution);
        return execution;
    }

    @Override
    public List<ServiceExecution> findByWorkerId(String workerId) {
        return storeById.values()
                .stream()
                .filter(execution -> execution.workerId().equals(workerId))
                .toList();
    }
}
