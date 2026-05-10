package com.veritrabajo.backend.serviceexecution.application;

import com.veritrabajo.backend.serviceexecution.domain.exception.ImageStorageException;
import org.springframework.http.MediaType;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Locale;
import java.util.UUID;

@RestController
@RequestMapping("/api/service-executions")
@CrossOrigin(origins = "*")
public class ServiceExecutionController {

    private final ServiceExecutionApplicationService service;

    public ServiceExecutionController(ServiceExecutionApplicationService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ServiceExecutionResponse> createExecution(
            @RequestBody CreateServiceExecutionRequest request) {
        ServiceExecutionResponse response = ServiceExecutionResponse.from(
                service.createExecution(request.clientId(), request.workerId())
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServiceExecutionResponse> getExecution(@PathVariable UUID id) {
        return ResponseEntity.ok(ServiceExecutionResponse.from(service.findExecution(id)));
    }

    @PutMapping("/{id}/begin")
    public ResponseEntity<ServiceExecutionResponse> beginExecution(@PathVariable UUID id) {
        return ResponseEntity.ok(ServiceExecutionResponse.from(service.beginExecution(id)));
    }

    @PostMapping(value = "/{id}/photos", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<ServiceExecutionResponse> addPhoto(
            @PathVariable UUID id,
            @RequestParam("file") MultipartFile file) {
        validateImageFile(file);
        ServiceExecutionResponse response = ServiceExecutionResponse.from(
                service.addPhoto(id, file.getOriginalFilename(), extractContent(file))
        );
        return ResponseEntity.ok(response);
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<ServiceExecutionResponse> completeExecution(
            @PathVariable UUID id,
            @RequestBody CompleteServiceExecutionRequest request) {
        return ResponseEntity.ok(ServiceExecutionResponse.from(
                service.completeExecution(id, request.clientRating(), request.clientComment())
        ));
    }

    private static void validateImageFile(MultipartFile file) {
        if (file == null || file.isEmpty()) {
            throw new IllegalArgumentException("Image file is required");
        }
        String contentType = file.getContentType();
        if (contentType == null || !contentType.toLowerCase(Locale.ROOT).startsWith("image/")) {
            throw new IllegalArgumentException("Only image files are allowed");
        }
    }

    private static byte[] extractContent(MultipartFile file) {
        try {
            return file.getBytes();
        } catch (IOException ex) {
            throw new ImageStorageException("Failed to read uploaded image content", ex);
        }
    }
}
