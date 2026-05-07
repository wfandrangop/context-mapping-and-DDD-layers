package com.veritrabajo.backend.workerprofile.infrastructure.web;

import com.veritrabajo.backend.workerprofile.application.WorkerProfileApplicationService;
import com.veritrabajo.backend.workerprofile.application.dto.RegisterWorkerRequest;
import com.veritrabajo.backend.workerprofile.application.dto.RegisterWorkerResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * REST adapter for worker registration.
 * Delegates to {@link WorkerProfileApplicationService}; domain rules stay outside the web layer.
 */
@RestController
@RequestMapping("/api/profiles")
public class WorkerProfileController {

    private final WorkerProfileApplicationService applicationService;

    public WorkerProfileController(
            WorkerProfileApplicationService applicationService
    ) {
        this.applicationService = applicationService;
    }

    /**
     * Registers a worker from the client payload.
     *
     * <p>{@code 201 Created} on success; {@code 400} for invalid input; {@code 409} for conflicts
     * (e.g. duplicate phone), handled by
     * {@link com.veritrabajo.backend.shared.api.ApiExceptionHandler}.
     */
    @PostMapping
    public ResponseEntity<RegisterWorkerResponse> registerWorker(
            @RequestBody RegisterWorkerRequest request
    ) {
        RegisterWorkerResponse response = applicationService.registerWorker(request);
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }
}
