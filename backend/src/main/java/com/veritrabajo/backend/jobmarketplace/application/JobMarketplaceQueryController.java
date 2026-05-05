package com.veritrabajo.backend.jobmarketplace.application;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/job-posts")
@CrossOrigin(origins = "*")
public class JobMarketplaceQueryController {

    private final JobMarketplaceApplicationService applicationService;

    public JobMarketplaceQueryController(JobMarketplaceApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @PostMapping
    public ResponseEntity<JobPostResponse> publishDemand(
            @RequestBody CreateJobPostRequest request
    ) {
        JobPostResponse response = JobPostResponse.from(applicationService.publishDemand(
                request.toCreation()
        ));
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<JobPostResponse> getDemand(@PathVariable UUID id) {
        JobPostResponse response = JobPostResponse.from(applicationService.getById(id));
        return ResponseEntity.ok(response);
    }

    @GetMapping("/open")
    public ResponseEntity<List<JobPostResponse>> openDemands() {
        List<JobPostResponse> response = applicationService.getOpenDemands()
                .stream()
                .map(JobPostResponse::from)
                .toList();
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/applications")
    public ResponseEntity<JobPostResponse> apply(
            @PathVariable UUID id,
            @RequestBody ApplyJobPostRequest request
    ) {
        JobPostResponse response = JobPostResponse.from(
                applicationService.apply(id, request.workerProfileId())
        );
        return ResponseEntity.ok(response);
    }

    @PostMapping("/{id}/select")
    public ResponseEntity<JobPostResponse> selectEmployee(
            @PathVariable UUID id,
            @RequestBody(required = false) SelectEmployeeRequest request
    ) {
        String workerProfileId = extractWorkerProfileId(request);
        JobPostResponse response = JobPostResponse.from(
                applicationService.select(id, workerProfileId)
        );
        return ResponseEntity.ok(response);
    }

    private static String extractWorkerProfileId(SelectEmployeeRequest request) {
        if (request == null) {
            return null;
        }
        return request.workerProfileId();
    }
}
