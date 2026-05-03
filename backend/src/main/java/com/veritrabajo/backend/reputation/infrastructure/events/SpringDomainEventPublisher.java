package com.veritrabajo.backend.reputation.infrastructure.events;

import com.veritrabajo.backend.reputation.domain.event.BadgeAwarded;
import com.veritrabajo.backend.reputation.domain.event.ReputationUpdated;
import com.veritrabajo.backend.reputation.domain.port.DomainEventPublisher;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * Infrastructure adapter that bridges domain events to Spring's
 * {@link ApplicationEventPublisher}.
 * <p>
 * This enables other bounded contexts (e.g., JobMarketplace) to subscribe
 * to reputation domain events via standard Spring {@code @EventListener}
 * mechanisms, fulfilling the Reputation (U) → JobMarketplace (D) Customer-Supplier
 * relationship defined in the context map.
 */
@Component
public class SpringDomainEventPublisher implements DomainEventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    public SpringDomainEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    public void publish(ReputationUpdated event) {
        applicationEventPublisher.publishEvent(event);
    }

    @Override
    public void publish(BadgeAwarded event) {
        applicationEventPublisher.publishEvent(event);
    }
}
