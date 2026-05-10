package com.veritrabajo.backend.reputation.domain.port;

import com.veritrabajo.backend.reputation.domain.event.BadgeAwarded;
import com.veritrabajo.backend.reputation.domain.event.ReputationUpdated;

public interface DomainEventPublisher {

    void publish(ReputationUpdated event);

    void publish(BadgeAwarded event);
}
