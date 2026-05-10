package com.veritrabajo.backend.workerprofile.application;

import com.veritrabajo.backend.workerprofile.application.dto.RegisterWorkerRequest;
import com.veritrabajo.backend.workerprofile.application.dto.RegisterWorkerResponse;
import com.veritrabajo.backend.workerprofile.domain.event.ProfileProfessionalized;
import com.veritrabajo.backend.workerprofile.domain.exception.WorkerProfileAlreadyExistsException;
import com.veritrabajo.backend.workerprofile.domain.model.AnalysisResult;
import com.veritrabajo.backend.workerprofile.domain.model.AuthUserId;
import com.veritrabajo.backend.workerprofile.domain.model.RawDescription;
import com.veritrabajo.backend.workerprofile.domain.model.WorkerProfile;
import com.veritrabajo.backend.workerprofile.domain.repository.WorkerProfileRepository;
import com.veritrabajo.backend.workerprofile.domain.service.IAAnalysisService;
import org.springframework.context.ApplicationEventPublisher;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Application service orchestrating worker registration.
 * Contains no domain rules; it coordinates validation, factory, repository, and response mapping.
 */
@Service
public class WorkerProfileApplicationService {

    private final IAAnalysisService analysisService;
    private final WorkerProfileRepository profileRepository;
    private final ApplicationEventPublisher eventPublisher;

    public WorkerProfileApplicationService(IAAnalysisService analysisService,
                                           WorkerProfileRepository profileRepository,
                                           ApplicationEventPublisher eventPublisher) {
        this.analysisService = analysisService;
        this.profileRepository = profileRepository;
        this.eventPublisher = eventPublisher;

    }

    @Transactional
    public RegisterWorkerResponse registerWorker(AuthUserId authUserId,
                                                 RegisterWorkerRequest request) {
        if (profileRepository.existsByAuthUserId(authUserId)) {
            throw new WorkerProfileAlreadyExistsException(authUserId);
        }
        validateNoDuplicate(request.getPhoneNumber());
        WorkerProfile profile = WorkerProfile.create(authUserId, request.getFullName(),
                request.getPhoneNumber());
        RawDescription description = RawDescription.of(request.getExperienceDescription());
        profile.assignRawDescription(description);
        AnalysisResult analysisResult = analysisService.analyze(description);
        profile.enrichWithAnalysis(analysisResult);
        WorkerProfile saved = profileRepository.save(profile);
        ProfileProfessionalized event = ProfileProfessionalized.of(saved.getId().asString(),
                saved.getFullName());
        eventPublisher.publishEvent(event);
        return RegisterWorkerResponse.success(saved.getId().asString());
    }

    public WorkerProfile getByAuthUserId(AuthUserId authUserId) {
        return profileRepository.findByAuthUserId(authUserId)
                .orElseThrow(() ->
                        new IllegalStateException("Worker profile not found for the current user"));
    }

    private void validateNoDuplicate(String phoneNumber) {
        if (profileRepository.existsByPhoneNumber(phoneNumber)) {
            throw new IllegalStateException("A profile with this phone number is already "
                    + "registered");
        }
    }

}
