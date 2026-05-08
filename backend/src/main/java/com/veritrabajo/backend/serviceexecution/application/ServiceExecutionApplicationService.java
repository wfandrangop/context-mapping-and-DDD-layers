package com.veritrabajo.backend.serviceexecution.application;

import com.veritrabajo.backend.serviceexecution.domain.exception.ServiceExecutionNotFoundException;
import com.veritrabajo.backend.serviceexecution.domain.event.ServiceExecutionFinalized;
import com.veritrabajo.backend.serviceexecution.domain.event.ServiceExecutionStarted;
import com.veritrabajo.backend.serviceexecution.domain.model.EvidencePhoto;
import com.veritrabajo.backend.serviceexecution.domain.model.ServiceExecution;
import com.veritrabajo.backend.serviceexecution.domain.port.DomainEventPublisher;
import com.veritrabajo.backend.serviceexecution.domain.port.ImageStoragePort;
import com.veritrabajo.backend.serviceexecution.domain.port.ServiceExecutionRepository;
import com.veritrabajo.backend.shared.contract.serviceexecution.ServiceExecutionCompleted;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

@Service
public class ServiceExecutionApplicationService {

    private static final int MIN_RATING = 1;
    private static final int MAX_RATING = 5;

    private final ServiceExecutionRepository repository;
    private final ImageStoragePort imageStoragePort;
    private final DomainEventPublisher eventPublisher;

    public ServiceExecutionApplicationService(
            ServiceExecutionRepository repository,
            ImageStoragePort imageStoragePort,
            DomainEventPublisher eventPublisher) {
        this.repository = repository;
        this.imageStoragePort = imageStoragePort;
        this.eventPublisher = eventPublisher;
    }

    @Transactional
    public ServiceExecution createExecution(String clientId, String workerId) {
        return repository.save(ServiceExecution.create(clientId, workerId));
    }

    @Transactional
    public ServiceExecution beginExecution(UUID id) {
        ServiceExecution execution = findOrThrow(id);
        ServiceExecutionStarted event = execution.begin();
        ServiceExecution saved = repository.save(execution);
        eventPublisher.publish(event);
        return saved;
    }

    @Transactional
    public ServiceExecution addPhoto(UUID id, String filename, byte[] content) {
        ServiceExecution execution = findOrThrow(id);
        String url = imageStoragePort.store(filename, content);
        execution.addPhoto(EvidencePhoto.of(url));
        return repository.save(execution);
    }

    @Transactional
    public ServiceExecution completeExecution(UUID id, int clientRating, String clientComment) {
        validateClientFeedback(clientRating, clientComment);
        ServiceExecution execution = findOrThrow(id);
        ServiceExecutionFinalized event = execution.complete();
        ServiceExecution saved = repository.save(execution);
        eventPublisher.publish(event);
        eventPublisher.publish(buildPartnershipEvent(event, clientRating, clientComment));
        return saved;
    }

    public ServiceExecution findExecution(UUID id) {
        return findOrThrow(id);
    }

    private static void validateClientFeedback(int clientRating, String clientComment) {
        if (clientRating < MIN_RATING || clientRating > MAX_RATING) {
            throw new IllegalArgumentException(
                    String.format("Client rating must be between %d and %d, got %d",
                            MIN_RATING, MAX_RATING, clientRating));
        }
        if (clientComment == null || clientComment.isBlank()) {
            throw new IllegalArgumentException("Client comment is required");
        }
    }

    private ServiceExecutionCompleted buildPartnershipEvent(
            ServiceExecutionFinalized event, int clientRating, String clientComment) {
        return new ServiceExecutionCompleted(
                event.executionId(),
                event.workerId(),
                clientRating,
                clientComment,
                true
        );
    }

    private ServiceExecution findOrThrow(UUID id) {
        return repository.findById(id)
                .orElseThrow(() -> new ServiceExecutionNotFoundException(id));
    }
}
