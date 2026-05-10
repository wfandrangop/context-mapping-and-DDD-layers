package com.veritrabajo.backend.reputation.infrastructure.events;

import com.veritrabajo.backend.reputation.domain.event.BadgeAwarded;
import com.veritrabajo.backend.reputation.domain.event.ReputationUpdated;
import com.veritrabajo.backend.reputation.domain.port.DomainEventPublisher;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

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
