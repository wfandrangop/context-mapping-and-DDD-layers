package com.veritrabajo.backend.jobmarketplace.infrastructure.events;

import com.veritrabajo.backend.jobmarketplace.domain.event.NewDemandPublished;
import com.veritrabajo.backend.jobmarketplace.domain.event.SelectedEmployee;
import com.veritrabajo.backend.jobmarketplace.domain.port.DomainEventPublisher;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

@Component("jobMarketplaceDomainEventPublisher")
public class SpringDomainEventPublisher implements DomainEventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    public SpringDomainEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    public void publish(NewDemandPublished event) {
        applicationEventPublisher.publishEvent(event);
    }

    @Override
    public void publish(SelectedEmployee event) {
        applicationEventPublisher.publishEvent(event);
    }
}
