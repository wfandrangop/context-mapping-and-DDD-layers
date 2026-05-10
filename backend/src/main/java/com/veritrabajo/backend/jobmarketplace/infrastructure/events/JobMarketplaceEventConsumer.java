package com.veritrabajo.backend.jobmarketplace.infrastructure.events;

import com.veritrabajo.backend.reputation.domain.event.BadgeAwarded;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

import java.util.Set;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Consumer that reacts to reputation updates to invalidate marketplace views.
 */
@Component
public class JobMarketplaceEventConsumer {

    private final Set<String> invalidatedProfiles = ConcurrentHashMap.newKeySet();

    @EventListener
    public void onBadgeAwarded(BadgeAwarded event) {
        invalidatedProfiles.add(event.profileId());
    }

    public Set<String> invalidatedProfiles() {
        return Set.copyOf(invalidatedProfiles);
    }
}
