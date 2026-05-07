package com.veritrabajo.backend.customer.domain.port;

import com.veritrabajo.backend.customer.domain.event.CustomerRegistered;
import com.veritrabajo.backend.customer.domain.event.ServiceRequestedByCustomer;

/**
 * Port for publishing Customer domain events.
 */
public interface DomainEventPublisher {

    void publish(CustomerRegistered event);

    void publish(ServiceRequestedByCustomer event);
}
