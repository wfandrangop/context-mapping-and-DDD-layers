package com.veritrabajo.backend.reputation.application;

import com.veritrabajo.backend.reputation.event.ProfessionalProfileCreated;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;

/**
 * Event listener for professional profile creation.
 * Creates initial reputation when a new profile is created.
 */
@Component
public class ProfessionalProfileEventListener {

    private final ReputationApplicationService reputationApplicationService;

    public ProfessionalProfileEventListener(ReputationApplicationService
                                                    reputationApplicationService) {
        this.reputationApplicationService = reputationApplicationService;
    }

    @EventListener
    public void onProfessionalProfileCreated(ProfessionalProfileCreated event) {
        reputationApplicationService.createIfNotExists(event.profileId());
    }
}
