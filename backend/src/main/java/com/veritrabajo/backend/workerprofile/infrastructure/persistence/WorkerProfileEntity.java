package com.veritrabajo.backend.workerprofile.infrastructure.persistence;

import jakarta.persistence.CollectionTable;
import jakarta.persistence.Column;
import jakarta.persistence.ElementCollection;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.Table;

import java.util.ArrayList;
import java.util.List;

/**
 * JPA mapping for worker profiles (infrastructure concern; keeps persistence annotations out
 * of domain).
 */
@Entity
@Table(name = "worker_profiles")
public class WorkerProfileEntity {

    @Id
    @Column(name = "id", nullable = false)
    private String id;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "phone_number", nullable = false, unique = true)
    private String phoneNumber;

    @Column(name = "raw_description", columnDefinition = "TEXT")
    private String rawDescription;

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "worker_occupations",
            joinColumns = @JoinColumn(name = "profile_id")
    )
    @Column(name = "occupation")
    private List<String> occupations = new ArrayList<>();

    @ElementCollection(fetch = FetchType.EAGER)
    @CollectionTable(
            name = "worker_technical_skills",
            joinColumns = @JoinColumn(name = "profile_id")
    )
    @Column(name = "skill")
    private List<String> technicalSkills = new ArrayList<>();

    protected WorkerProfileEntity() {
    }

    public WorkerProfileEntity(
            String id,
            String fullName,
            String phoneNumber
    ) {
        this.id = id;
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
    }

    public String getId() {
        return id;
    }

    public String getFullName() {
        return fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public String getRawDescription() {
        return rawDescription;
    }

    public void setRawDescription(String rawDescription) {
        this.rawDescription = rawDescription;
    }

    public List<String> getOccupations() {
        return occupations;
    }

    public void setOccupations(List<String> occupations) {
        this.occupations = occupations;
    }

    public List<String> getTechnicalSkills() {
        return technicalSkills;
    }

    public void setTechnicalSkills(List<String> technicalSkills) {
        this.technicalSkills = technicalSkills;
    }
}
