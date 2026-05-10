package com.veritrabajo.backend.serviceexecution.infrastructure.persistence.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Embeddable;

import java.time.Instant;
import java.util.UUID;

@Embeddable
public class EvidencePhotoEmbeddable {

    @Column(nullable = false)
    private UUID photoId;

    @Column(nullable = false, length = 1024)
    private String url;

    @Column(nullable = false)
    private Instant takenAt;

    public UUID getPhotoId() {
        return photoId;
    }

    public void setPhotoId(UUID photoId) {
        this.photoId = photoId;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Instant getTakenAt() {
        return takenAt;
    }

    public void setTakenAt(Instant takenAt) {
        this.takenAt = takenAt;
    }
}
