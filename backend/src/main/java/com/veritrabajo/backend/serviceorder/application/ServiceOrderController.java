package com.veritrabajo.backend.serviceorder.application;

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
import java.util.UUID;

/**
 * REST controller exposing the ServiceOrder API.
 */
@RestController
@RequestMapping("/api/service-orders")
@CrossOrigin(origins = "*")
public class ServiceOrderController {

    private final ServiceOrderApplicationService service;

    public ServiceOrderController(ServiceOrderApplicationService service) {
        this.service = service;
    }

    @PostMapping
    public ResponseEntity<ServiceOrderResponse> createOrder(
            @RequestBody CreateOrderRequest request) {
        ServiceOrderResponse response = ServiceOrderResponse.from(
                service.createOrder(request.clientId(), request.workerId())
        );
        return ResponseEntity.status(HttpStatus.CREATED).body(response);
    }

    @GetMapping("/{id}")
    public ResponseEntity<ServiceOrderResponse> getOrder(@PathVariable UUID id) {
        return ResponseEntity.ok(ServiceOrderResponse.from(service.findOrder(id)));
    }

    @PutMapping("/{id}/begin")
    public ResponseEntity<ServiceOrderResponse> beginOrder(@PathVariable UUID id) {
        return ResponseEntity.ok(ServiceOrderResponse.from(service.beginOrder(id)));
    }

    @PostMapping("/{id}/photos")
    public ResponseEntity<ServiceOrderResponse> addPhoto(
            @PathVariable UUID id,
            @RequestParam("file") MultipartFile file) {
        try {
            ServiceOrderResponse response = ServiceOrderResponse.from(
                    service.addPhoto(id, file.getOriginalFilename(), file.getBytes())
            );
            return ResponseEntity.ok(response);
        } catch (IOException ex) {
            return ResponseEntity.badRequest().build();
        }
    }

    @PutMapping("/{id}/complete")
    public ResponseEntity<ServiceOrderResponse> completeOrder(@PathVariable UUID id) {
        return ResponseEntity.ok(ServiceOrderResponse.from(service.completeOrder(id)));
    }
}
