package com.veritrabajo.backend.serviceorder.domain.port;

import com.veritrabajo.backend.reputation.application.integration.ServiceExecutionCompleted;
import com.veritrabajo.backend.serviceorder.domain.event.ServiceOrderFinalized;
import com.veritrabajo.backend.serviceorder.domain.event.ServiceOrderStarted;

/**
 * Port (outbound) for publishing events from the ServiceOrder context.
 * Infrastructure adapters implement this interface to bridge events to the
 * messaging/event system (e.g., Spring ApplicationEventPublisher).
 * <p>
 * Includes both internal domain events ({@link ServiceOrderStarted},
 * {@link ServiceOrderFinalized}) and the {@link ServiceExecutionCompleted}
 * integration event consumed by Reputation (Partnership relationship).
 */
public interface DomainEventPublisher {

    /**
     * Publishes a {@link ServiceOrderStarted} domain event.
     *
     * @param event the service order started event to publish
     */
    void publish(ServiceOrderStarted event);

    /**
     * Publishes a {@link ServiceOrderFinalized} domain event.
     *
     * @param event the service order finalized event to publish
     */
    void publish(ServiceOrderFinalized event);

    /**
     * Publishes the {@link ServiceExecutionCompleted} integration event,
     * consumed by the Reputation context (Partnership relationship).
     *
     * @param event the integration event to publish
     */
    void publish(ServiceExecutionCompleted event);
}
