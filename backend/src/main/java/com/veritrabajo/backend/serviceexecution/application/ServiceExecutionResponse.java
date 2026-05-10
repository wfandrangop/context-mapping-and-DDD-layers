package com.veritrabajo.backend.serviceexecution.application;

import com.veritrabajo.backend.serviceexecution.domain.model.EvidencePhoto;
import com.veritrabajo.backend.serviceexecution.domain.model.ServiceExecution;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

public record ServiceExecutionResponse(
        UUID id,
        String clientId,
        String workerId,
        String status,
        List<String> photoUrls) {

    public static ServiceExecutionResponse from(ServiceExecution execution) {
        List<String> urls = execution.photos().stream()
                .map(EvidencePhoto::url)
                .collect(Collectors.toList());
        return new ServiceExecutionResponse(
                execution.id(), execution.clientId(), execution.workerId(),
                execution.status().name(), urls
        );
    }
}
