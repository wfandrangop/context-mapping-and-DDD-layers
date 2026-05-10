package com.veritrabajo.backend.serviceexecution.infrastructure.persistence;

import com.veritrabajo.backend.serviceexecution.domain.model.EvidencePhoto;
import com.veritrabajo.backend.serviceexecution.domain.model.ServiceExecution;
import com.veritrabajo.backend.serviceexecution.domain.model.ServiceExecutionData;
import com.veritrabajo.backend.serviceexecution.infrastructure.persistence.entity.EvidencePhotoEmbeddable;
import com.veritrabajo.backend.serviceexecution.infrastructure.persistence.entity.ServiceExecutionEntity;

import java.util.List;

public final class ServiceExecutionMapper {

    private ServiceExecutionMapper() {
    }

    public static ServiceExecution toDomain(ServiceExecutionEntity entity) {
        ServiceExecutionData data = new ServiceExecutionData(
                entity.getId(),
                entity.getClientId(),
                entity.getWorkerId(),
                entity.getStatus(),
                mapPhotosToDomain(entity.getPhotos())
        );
        return ServiceExecution.rehydrate(data);
    }

    public static void updateEntity(ServiceExecutionEntity target, ServiceExecution source) {
        target.setId(source.id());
        target.setClientId(source.clientId());
        target.setWorkerId(source.workerId());
        target.setStatus(source.status());
        target.setPhotos(mapPhotosToEntity(source.photos()));
    }

    private static List<EvidencePhoto> mapPhotosToDomain(List<EvidencePhotoEmbeddable> photos) {
        return photos.stream()
                .map(photo -> new EvidencePhoto(
                        photo.getPhotoId(),
                        photo.getUrl(),
                        photo.getTakenAt()))
                .toList();
    }

    private static List<EvidencePhotoEmbeddable> mapPhotosToEntity(List<EvidencePhoto> photos) {
        return new java.util.ArrayList<>(photos.stream()
                .map(ServiceExecutionMapper::toEmbeddable)
                .toList());
    }

    private static EvidencePhotoEmbeddable toEmbeddable(EvidencePhoto photo) {
        EvidencePhotoEmbeddable embeddable = new EvidencePhotoEmbeddable();
        embeddable.setPhotoId(photo.id());
        embeddable.setUrl(photo.url());
        embeddable.setTakenAt(photo.takenAt());
        return embeddable;
    }
}
