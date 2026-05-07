package com.veritrabajo.backend.workerprofile.domain.factory;

import com.veritrabajo.backend.workerprofile.domain.event.ProfileProfessionalized;
import com.veritrabajo.backend.workerprofile.domain.model.AnalysisResult;
import com.veritrabajo.backend.workerprofile.domain.model.Occupation;
import com.veritrabajo.backend.workerprofile.domain.model.RawDescription;
import com.veritrabajo.backend.workerprofile.domain.model.TechnicalSkill;
import com.veritrabajo.backend.workerprofile.domain.model.WorkerProfile;
import com.veritrabajo.backend.workerprofile.domain.service.IAAnalysisService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Component;

/**
 * Builds a complete {@link WorkerProfile}: runs AI analysis for occupations/skills and publishes
 * {@link ProfileProfessionalized}.
 */
@Component
public class ProfileFactory {

    private final IAAnalysisService analysisService;
    private final ApplicationEventPublisher eventPublisher;

    public ProfileFactory(
            IAAnalysisService analysisService,
            ApplicationEventPublisher eventPublisher
    ) {
        this.analysisService = analysisService;
        this.eventPublisher = eventPublisher;
    }

    /**
     * Creates and enriches a profile from identity fields plus free-text experience description.
     *
     * @param fullName    worker display name
     * @param phoneNumber contact phone (unique at persistence layer)
     * @param rawText     free-text experience narrative for AI extraction
     */
    public WorkerProfile createFromDescription(
            String fullName,
            String phoneNumber,
            String rawText
    ) {
        WorkerProfile profile = WorkerProfile.create(fullName, phoneNumber);
        RawDescription description = RawDescription.of(rawText);
        profile.assignRawDescription(description);
        AnalysisResult result = analysisService.analyze(description);
        applyAnalysisToProfile(profile, result);
        publishProfessionalizedEvent(profile);
        return profile;
    }

    private void applyAnalysisToProfile(WorkerProfile profile, AnalysisResult result) {
        for (Occupation occupation : result.getOccupations()) {
            profile.addOccupation(occupation);
        }
        for (TechnicalSkill skill : result.getTechnicalSkills()) {
            profile.addTechnicalSkill(skill);
        }
    }

    private void publishProfessionalizedEvent(WorkerProfile profile) {
        ProfileProfessionalized event = ProfileProfessionalized.of(
                profile.getId(),
                profile.getFullName()
        );
        eventPublisher.publishEvent(event);
    }
}
