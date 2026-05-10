package com.veritrabajo.backend.reputation.application.integration;

import com.veritrabajo.backend.reputation.application.ReputationApplicationService;
import com.veritrabajo.backend.shared.contract.serviceexecution.ServiceExecutionCompleted;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

@Component
public class ReputationIntegrationEventListeners {

    private final ReputationApplicationService applicationService;

    public ReputationIntegrationEventListeners(
            ReputationApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT,
            fallbackExecution = true)
    public void onServiceExecutionCompleted(
            ServiceExecutionCompleted event) {
        applicationService.processServiceCompletion(event);
    }

    @EventListener
    public void onProfessionalProfileCreated(
            ProfessionalProfileCreated event) {
        applicationService.createIfNotExists(event.profileId());
    }
}
