package com.veritrabajo.backend.serviceorder.infraestructure.persistence;

import com.veritrabajo.backend.serviceorder.domain.model.ServiceOrder;
import com.veritrabajo.backend.serviceorder.repository.ServiceOrderRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

/**
 * In-memory implementation of ServiceOrderRepository using a ConcurrentHashMap.
 */
@Repository
public class InMemoryServiceOrderRepository implements ServiceOrderRepository {

    private final Map<UUID, ServiceOrder> store = new ConcurrentHashMap<>();

    @Override
    public Optional<ServiceOrder> findById(UUID id) {
        return Optional.ofNullable(store.get(id));
    }

    @Override
    public ServiceOrder save(ServiceOrder order) {
        store.put(order.id(), order);
        return order;
    }

    @Override
    public List<ServiceOrder> findByWorkerId(String workerId) {
        return store.values().stream()
                .filter(order -> order.workerId().equals(workerId))
                .collect(Collectors.toList());
    }
}
