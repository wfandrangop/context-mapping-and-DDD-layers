package com.veritrabajo.backend.serviceorder.application;

import com.veritrabajo.backend.reputation.application.integration.ServiceExecutionCompleted;
import com.veritrabajo.backend.serviceorder.domain.event.ServiceOrderFinalized;
import com.veritrabajo.backend.serviceorder.domain.event.ServiceOrderStarted;
import com.veritrabajo.backend.serviceorder.domain.model.EvidencePhoto;
import com.veritrabajo.backend.serviceorder.domain.model.ServiceOrder;
import com.veritrabajo.backend.serviceorder.domain.port.DomainEventPublisher;
import com.veritrabajo.backend.serviceorder.domain.port.ImageStoragePort;
import com.veritrabajo.backend.serviceorder.domain.port.ServiceOrderRepository;
import org.springframework.stereotype.Service;

import java.util.UUID;

/**
 * Application service for ServiceOrder use cases.
 * <p>
 * Publishes internal domain events and the {@link ServiceExecutionCompleted}
 * integration event to the Reputation bounded context (Partnership relationship)
 * via {@link DomainEventPublisher}.
 */
@Service
public class ServiceOrderApplicationService {

    private final ServiceOrderRepository repository;
    private final ImageStoragePort imageStoragePort;
    private final DomainEventPublisher eventPublisher;

    public ServiceOrderApplicationService(
            ServiceOrderRepository repository,
            ImageStoragePort imageStoragePort,
            DomainEventPublisher eventPublisher) {
        this.repository = repository;
        this.imageStoragePort = imageStoragePort;
        this.eventPublisher = eventPublisher;
    }

    public ServiceOrder createOrder(String clientId, String workerId) {
        return repository.save(ServiceOrder.create(clientId, workerId));
    }

    public ServiceOrder beginOrder(UUID id) {
        ServiceOrder order = findOrThrow(id);
        ServiceOrderStarted event = order.begin();
        ServiceOrder saved = repository.save(order);
        eventPublisher.publish(event);
        return saved;
    }

    public ServiceOrder addPhoto(UUID id, String filename, byte[] content) {
        ServiceOrder order = findOrThrow(id);
        String url = imageStoragePort.store(filename, content);
        order.addPhoto(EvidencePhoto.of(url));
        return repository.save(order);
    }

    public ServiceOrder completeOrder(UUID id, int clientRating, String clientComment) {
        ServiceOrder order = findOrThrow(id);
        ServiceOrderFinalized event = order.complete();
        ServiceOrder saved = repository.save(order);
        eventPublisher.publish(event);
        eventPublisher.publish(buildPartnershipEvent(event, clientRating, clientComment));
        return saved;
    }

    public ServiceOrder findOrder(UUID id) {
        return findOrThrow(id);
    }

    private ServiceExecutionCompleted buildPartnershipEvent(
            ServiceOrderFinalized event, int clientRating, String clientComment) {
        return new ServiceExecutionCompleted(
                event.orderId(),
                event.workerId(),
                clientRating,
                clientComment,
                true
        );
    }

    private ServiceOrder findOrThrow(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("Order not found: " + id));
    }
}
