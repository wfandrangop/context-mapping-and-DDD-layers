package com.veritrabajo.backend.workerprofile.infrastructure.web;

import com.veritrabajo.backend.workerprofile.application.WorkerProfileApplicationService;
import com.veritrabajo.backend.workerprofile.application.dto.RegisterWorkerRequest;
import com.veritrabajo.backend.workerprofile.application.dto.RegisterWorkerResponse;
import com.veritrabajo.backend.workerprofile.application.dto.WorkerProfileResponse;
import com.veritrabajo.backend.workerprofile.domain.model.AuthUserId;
import com.veritrabajo.backend.workerprofile.domain.model.WorkerProfile;
import com.veritrabajo.backend.workerprofile.domain.port.AuthenticatedIdentityProvider;
import com.veritrabajo.backend.workerprofile.domain.port.ReputationProvider;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/profiles")
public class WorkerProfileController {

    private final WorkerProfileApplicationService applicationService;
    private final AuthenticatedIdentityProvider identityProvider;
    private final ReputationProvider reputationProvider;

    public WorkerProfileController(
            WorkerProfileApplicationService applicationService,
            AuthenticatedIdentityProvider identityProvider,
            ReputationProvider reputationProvider
    ) {
        this.applicationService = applicationService;
        this.identityProvider = identityProvider;
        this.reputationProvider = reputationProvider;
    }

    @PostMapping
    public ResponseEntity<RegisterWorkerResponse> registerWorker(
            @RequestBody RegisterWorkerRequest request
    ) {
        AuthUserId authUserId = identityProvider.currentAuthUserId();
        RegisterWorkerResponse response = applicationService.registerWorker(authUserId, request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/me")
    public ResponseEntity<WorkerProfileResponse> getMyProfile() {
        AuthUserId authUserId = identityProvider.currentAuthUserId();
        WorkerProfile profile = applicationService.getByAuthUserId(authUserId);
        int score = reputationProvider.confidenceScore(profile.getId().asString());
        return ResponseEntity.ok(WorkerProfileResponse.from(profile, score));
    }
}
