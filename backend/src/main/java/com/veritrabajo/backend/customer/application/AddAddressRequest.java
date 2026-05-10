package com.veritrabajo.backend.customer.application;

import com.veritrabajo.backend.customer.domain.model.Location;

public record AddAddressRequest(
        String label,
        double latitude,
        double longitude,
        String description
) {
    public Location toLocation() {
        return Location.of(latitude, longitude, description);
    }
}
