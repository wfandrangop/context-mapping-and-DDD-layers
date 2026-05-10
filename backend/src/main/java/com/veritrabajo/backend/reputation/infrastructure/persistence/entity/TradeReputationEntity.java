package com.veritrabajo.backend.reputation.infrastructure.persistence.entity;

import com.veritrabajo.backend.reputation.domain.model.Badge;
import jakarta.persistence.CascadeType;
import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.persistence.Version;

import java.util.ArrayList;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Set;
import java.util.UUID;

@Entity
@Table(name = "trade_reputations")
public class TradeReputationEntity {

    @Id
    @Column(nullable = false, updatable = false)
    private UUID id;

    @Column(unique = true, nullable = false)
    private String profileId;

    @Column(nullable = false)
    private int confidenceScore;

    @Column(nullable = false)
    private int successfulJobs;

    @Column(nullable = false)
    private int cancelledJobs;

    @Version
    private Long version;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
        name = "reputation_badges",
        joinColumns = @JoinColumn(name = "reputation_id")
    )
    @Enumerated(EnumType.STRING)
    @Column(name = "badge")
    private Set<Badge> badges = new LinkedHashSet<>();

    @OneToMany(
        cascade = CascadeType.ALL,
        orphanRemoval = true,
        fetch = FetchType.EAGER
    )
    @JoinColumn(name = "reputation_id")
    private List<ReviewEntity> reviews = new ArrayList<>();

    public TradeReputationEntity() {
        // Required by JPA
    }

    public UUID getId() {
        return id;
    }

    public void setId(UUID id) {
        this.id = id;
    }

    public String getProfileId() {
        return profileId;
    }

    public void setProfileId(String profileId) {
        this.profileId = profileId;
    }

    public int getConfidenceScore() {
        return confidenceScore;
    }

    public void setConfidenceScore(int confidenceScore) {
        this.confidenceScore = confidenceScore;
    }

    public int getSuccessfulJobs() {
        return successfulJobs;
    }

    public void setSuccessfulJobs(int successfulJobs) {
        this.successfulJobs = successfulJobs;
    }

    public int getCancelledJobs() {
        return cancelledJobs;
    }

    public void setCancelledJobs(int cancelledJobs) {
        this.cancelledJobs = cancelledJobs;
    }

    public Set<Badge> getBadges() {
        return badges;
    }

    public void setBadges(Set<Badge> badges) {
        this.badges = badges;
    }

    public List<ReviewEntity> getReviews() {
        return reviews;
    }

    public void setReviews(List<ReviewEntity> reviews) {
        this.reviews = reviews;
    }

    public Long getVersion() {
        return version;
    }
}
