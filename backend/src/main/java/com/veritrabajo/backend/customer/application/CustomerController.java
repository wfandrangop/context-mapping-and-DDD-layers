package com.veritrabajo.backend.customer.application;

import com.veritrabajo.backend.customer.domain.model.Customer;
import com.veritrabajo.backend.customer.domain.model.SavedAddress;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.UUID;

@RestController
@RequestMapping("/api/customers")
@CrossOrigin(origins = "*")
public class CustomerController {

    private final CustomerApplicationService applicationService;

    public CustomerController(CustomerApplicationService applicationService) {
        this.applicationService = applicationService;
    }

    @PostMapping
    public ResponseEntity<CustomerResponse> register(
            @RequestBody RegisterCustomerRequest request
    ) {
        Customer customer = applicationService.register(request.toCreation());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(CustomerResponse.from(customer));
    }

    @GetMapping("/{id}")
    public ResponseEntity<CustomerResponse> getById(@PathVariable UUID id) {
        Customer customer = applicationService.getById(id);
        return ResponseEntity.ok(CustomerResponse.from(customer));
    }

    @PutMapping("/{id}/preferences")
    public ResponseEntity<CustomerResponse> updatePreferences(
            @PathVariable UUID id,
            @RequestBody UpdatePreferencesRequest request
    ) {
        Customer customer = applicationService.updatePreferences(
                id, request.toPreferences());
        return ResponseEntity.ok(CustomerResponse.from(customer));
    }

    @PostMapping("/{id}/addresses")
    public ResponseEntity<CustomerResponse.AddressResponse> addAddress(
            @PathVariable UUID id,
            @RequestBody AddAddressRequest request
    ) {
        SavedAddress address = applicationService.addAddress(
                id, request.label(), request.toLocation());
        return ResponseEntity
                .status(HttpStatus.CREATED)
                .body(CustomerResponse.AddressResponse.from(address));
    }

    @PostMapping("/{id}/service-requests")
    public ResponseEntity<Void> requestService(
            @PathVariable UUID id,
            @RequestBody RequestServiceRequest request
    ) {
        applicationService.requestService(id, request.jobPostId(), request.addressId());
        return ResponseEntity.accepted().build();
    }
}
