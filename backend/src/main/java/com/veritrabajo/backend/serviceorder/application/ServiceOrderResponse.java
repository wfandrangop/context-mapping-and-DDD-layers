package com.veritrabajo.backend.serviceorder.application;

import com.veritrabajo.backend.serviceorder.domain.model.EvidencePhoto;
import com.veritrabajo.backend.serviceorder.domain.model.ServiceOrder;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

/**
 * Read model returned by the ServiceOrder API.
 */
public record ServiceOrderResponse(
        UUID id,
        String clientId,
        String workerId,
        String status,
        List<String> photoUrls) {

    public static ServiceOrderResponse from(ServiceOrder order) {
        List<String> urls = order.photos().stream()
                .map(EvidencePhoto::url)
                .collect(Collectors.toList());
        return new ServiceOrderResponse(
                order.id(), order.clientId(), order.workerId(),
                order.status().name(), urls
        );
    }
}
