package com.veritrabajo.backend.reputation.domain.port;

import com.veritrabajo.backend.reputation.domain.event.BadgeAwarded;
import com.veritrabajo.backend.reputation.domain.event.ReputationUpdated;

/**
 * Port (outbound) for publishing domain events from the reputation context.
 * Infrastructure adapters implement this interface to bridge domain events
 * to the actual messaging/event system (e.g., Spring ApplicationEventPublisher).
 */
public interface DomainEventPublisher {

    /**
     * Publishes a {@link ReputationUpdated} domain event.
     *
     * @param event the reputation updated event to publish
     */
    void publish(ReputationUpdated event);

    /**
     * Publishes a {@link BadgeAwarded} domain event.
     *
     * @param event the badge awarded event to publish
     */
    void publish(BadgeAwarded event);
}
