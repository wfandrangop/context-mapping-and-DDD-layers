package com.veritrabajo.backend.reputation.application.integration;

import com.veritrabajo.backend.reputation.application.ReputationApplicationService;
import com.veritrabajo.backend.shared.contract.serviceexecution.ServiceExecutionCompleted;
import org.springframework.context.event.EventListener;
import org.springframework.stereotype.Component;
import org.springframework.transaction.event.TransactionPhase;
import org.springframework.transaction.event.TransactionalEventListener;

/**
 * Adapter that listens for integration events emitted by upstream
 * bounded contexts (e.g., ServiceExecution, ProfessionalProfile)
 * and delegates processing to the application service.
 * <p>
 * Consolidating listeners here avoids file bloat while maintaining
 * clear boundaries between external events and core domain logic.
 */
@Component
public class ReputationIntegrationEventListeners {

    private final ReputationApplicationService applicationService;

    public ReputationIntegrationEventListeners(
            ReputationApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    /**
     * Reacts to a service execution being completed upstream.
     * <p>
     * Processed only after the upstream transaction commits, so a failure
     * here cannot revert a valid execution finalization (Partnership pattern).
     *
     * @param event the integration event carrying execution details
     */
    @TransactionalEventListener(phase = TransactionPhase.AFTER_COMMIT,
            fallbackExecution = true)
    public void onServiceExecutionCompleted(
            ServiceExecutionCompleted event) {
        applicationService.processServiceCompletion(event);
    }

    /**
     * Reacts to a professional profile being created upstream.
     *
     * @param event the integration event carrying profile details
     */
    @EventListener
    public void onProfessionalProfileCreated(
            ProfessionalProfileCreated event) {
        applicationService.createIfNotExists(event.profileId());
    }
}
