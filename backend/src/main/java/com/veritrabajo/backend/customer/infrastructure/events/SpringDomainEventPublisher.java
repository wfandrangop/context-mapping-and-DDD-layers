package com.veritrabajo.backend.customer.infrastructure.events;

import com.veritrabajo.backend.customer.domain.event.CustomerRegistered;
import com.veritrabajo.backend.customer.domain.event.ServiceRequestedByCustomer;
import com.veritrabajo.backend.customer.domain.port.DomainEventPublisher;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * Infrastructure adapter that publishes Customer events through Spring.
 */
@Component("customerDomainEventPublisher")
public class SpringDomainEventPublisher implements DomainEventPublisher {

    private final ApplicationEventPublisher applicationEventPublisher;

    public SpringDomainEventPublisher(ApplicationEventPublisher applicationEventPublisher) {
        this.applicationEventPublisher = applicationEventPublisher;
    }

    @Override
    public void publish(CustomerRegistered event) {
        applicationEventPublisher.publishEvent(event);
    }

    @Override
    public void publish(ServiceRequestedByCustomer event) {
        applicationEventPublisher.publishEvent(event);
    }
}
