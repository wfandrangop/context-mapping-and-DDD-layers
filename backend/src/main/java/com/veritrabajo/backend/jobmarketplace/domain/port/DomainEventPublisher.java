package com.veritrabajo.backend.jobmarketplace.domain.port;

import com.veritrabajo.backend.jobmarketplace.domain.event.NewDemandPublished;
import com.veritrabajo.backend.jobmarketplace.domain.event.SelectedEmployee;

/**
 * Port for publishing JobMarketplace domain events.
 */
public interface DomainEventPublisher {

    void publish(NewDemandPublished event);

    void publish(SelectedEmployee event);
}
