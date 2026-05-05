package com.veritrabajo.backend.workerprofile.application;

import com.veritrabajo.backend.workerprofile.application.dto.RegisterWorkerRequest;
import com.veritrabajo.backend.workerprofile.application.dto.RegisterWorkerResponse;
import com.veritrabajo.backend.workerprofile.domain.factory.ProfileFactory;
import com.veritrabajo.backend.workerprofile.domain.model.WorkerProfile;
import com.veritrabajo.backend.workerprofile.domain.repository.WorkerProfileRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Application service orchestrating worker registration.
 * Contains no domain rules; it coordinates validation, factory, repository, and response mapping.
 */
@Service
public class WorkerProfileApplicationService {

    private final ProfileFactory profileFactory;
    private final WorkerProfileRepository profileRepository;

    public WorkerProfileApplicationService(
            ProfileFactory profileFactory,
            WorkerProfileRepository profileRepository
    ) {
        this.profileFactory = profileFactory;
        this.profileRepository = profileRepository;
    }

    /**
     * Registers a new worker: rejects duplicates, builds the profile via AI enrichment,
     * persists it.
     *
     * @param request payload from the client
     * @return success payload including generated profile id
     */
    @Transactional
    public RegisterWorkerResponse registerWorker(
            RegisterWorkerRequest request
    ) {
        validateNoDuplicate(request.getPhoneNumber());
        WorkerProfile profile = profileFactory.createFromDescription(
                request.getFullName(),
                request.getPhoneNumber(),
                request.getExperienceDescription()
        );
        WorkerProfile saved = profileRepository.save(profile);
        return RegisterWorkerResponse.success(saved.getId());
    }

    /**
     * Ensures no other worker is registered with the same phone number.
     *
     * @throws IllegalStateException when the phone number is already taken
     */
    private void validateNoDuplicate(String phoneNumber) {
        if (profileRepository.existsByPhoneNumber(phoneNumber)) {
            throw new IllegalStateException(
                    "A profile with this phone number is already registered"
            );
        }
    }
}
