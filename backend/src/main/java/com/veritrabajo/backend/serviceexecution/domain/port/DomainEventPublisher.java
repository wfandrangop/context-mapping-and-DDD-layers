package com.veritrabajo.backend.serviceexecution.domain.port;

import com.veritrabajo.backend.serviceexecution.domain.event.ServiceExecutionFinalized;
import com.veritrabajo.backend.serviceexecution.domain.event.ServiceExecutionStarted;
import com.veritrabajo.backend.shared.contract.serviceexecution.ServiceExecutionCompleted;

public interface DomainEventPublisher {

    void publish(ServiceExecutionStarted event);

    void publish(ServiceExecutionFinalized event);

    void publish(ServiceExecutionCompleted event);
}
