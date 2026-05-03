package com.veritrabajo.backend.serviceorder.application;

import com.veritrabajo.backend.serviceorder.domain.model.EvidencePhoto;
import com.veritrabajo.backend.serviceorder.domain.model.ServiceOrder;
import com.veritrabajo.backend.serviceorder.repository.ServiceOrderRepository;
import com.veritrabajo.backend.serviceorder.service.ImageStoragePort;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Application service for ServiceOrder use cases.
 */
@Service
public class ServiceOrderApplicationService {

    private final ServiceOrderRepository repository;
    private final ImageStoragePort imageStoragePort;

    public ServiceOrderApplicationService(
            ServiceOrderRepository repository,
            ImageStoragePort imageStoragePort) {
        this.repository = repository;
        this.imageStoragePort = imageStoragePort;
    }

    public ServiceOrder createOrder(String clientId, String workerId) {
        return repository.save(ServiceOrder.create(clientId, workerId));
    }

    public ServiceOrder beginOrder(UUID id) {
        ServiceOrder order = findOrThrow(id);
        order.begin();
        return repository.save(order);
    }

    public ServiceOrder addPhoto(UUID id, String filename, byte[] content) {
        ServiceOrder order = findOrThrow(id);
        String url = imageStoragePort.store(filename, content);
        order.addPhoto(EvidencePhoto.of(url));
        return repository.save(order);
    }

    public ServiceOrder completeOrder(UUID id) {
        ServiceOrder order = findOrThrow(id);
        order.complete();
        return repository.save(order);
    }

    public ServiceOrder findOrder(UUID id) {
        return findOrThrow(id);
    }

    private ServiceOrder findOrThrow(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + id));
    }
}
