package com.veritrabajo.backend.customer.domain.port;

import com.veritrabajo.backend.customer.domain.event.CustomerRegistered;
import com.veritrabajo.backend.customer.domain.event.ServiceRequestedByCustomer;

public interface DomainEventPublisher {

    void publish(CustomerRegistered event);

    void publish(ServiceRequestedByCustomer event);
}
