package com.veritrabajo.backend.workerprofile.domain.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

public final class WorkerProfile {

    private final WorkerId id;
    private final AuthUserId authUserId;
    private final String fullName;
    private final String phoneNumber;
    private final List<Occupation> occupations;
    private final List<TechnicalSkill> technicalSkills;
    private final List<WorkHistory> workHistories;
    private RawDescription rawDescription;
    private OwnedTools ownedTools;

    private WorkerProfile(WorkerProfileSnapshot snapshot) {
        this.id = Objects.requireNonNull(snapshot.id(), "id is required");
        this.authUserId = Objects.requireNonNull(snapshot.authUserId(), "authUserId is required");
        requireNonBlank(snapshot.fullName(), "Full name cannot be blank");
        requireNonBlank(snapshot.phoneNumber(), "Phone number cannot be blank");
        this.fullName = snapshot.fullName().trim();
        this.phoneNumber = snapshot.phoneNumber().trim();
        this.ownedTools = OwnedTools.empty();
        this.occupations = new ArrayList<>();
        this.technicalSkills = new ArrayList<>();
        this.workHistories = new ArrayList<>();
    }

    /**
     * Factory used when registering a brand-new profile. Generates a fresh {@link WorkerId}
     * and binds it to the supplied {@link AuthUserId}.
     */
    public static WorkerProfile create(AuthUserId authUserId, String fullName, String phoneNumber) {
        Objects.requireNonNull(authUserId, "authUserId is required");
        return new WorkerProfile(new WorkerProfileSnapshot(
                WorkerId.generate(), authUserId, fullName, phoneNumber));
    }

    /**
     * Factory used when rehydrating an existing profile from persistence; preserves the
     * stored identity instead of generating a new one.
     */
    public static WorkerProfile restore(WorkerProfileSnapshot snapshot) {
        Objects.requireNonNull(snapshot, "snapshot is required");
        return new WorkerProfile(snapshot);
    }

    public void assignRawDescription(RawDescription description) {
        if (this.rawDescription != null) {
            throw new IllegalStateException("Raw description was already assigned to this profile");
        }
        this.rawDescription = description;
    }

    public void addOccupation(Occupation occupation) {
        if (occupation == null) {
            throw new IllegalArgumentException("Occupation cannot be null");
        }
        if (occupations.contains(occupation)) {
            throw new IllegalArgumentException("Occupation already present on profile: "
                    + occupation.getTradeName());
        }
        occupations.add(occupation);
    }

    public void addTechnicalSkill(TechnicalSkill skill) {
        if (skill == null) {
            throw new IllegalArgumentException("Technical skill cannot be null");
        }
        if (technicalSkills.contains(skill)) {
            return;
        }
        technicalSkills.add(skill);
    }

    public void enrichWithAnalysis(AnalysisResult result) {
        if (result == null) {
            return;
        }
        result.getOccupations().forEach(this::addOccupation);
        result.getTechnicalSkills().forEach(this::addTechnicalSkill);
    }

    public WorkerId getId() {
        return id;
    }

    public AuthUserId getAuthUserId() {
        return authUserId;
    }

    public String getFullName() {
        return fullName;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public RawDescription getRawDescription() {
        return rawDescription;
    }

    public OwnedTools getOwnedTools() {
        return ownedTools;
    }

    public List<Occupation> getOccupations() {
        return Collections.unmodifiableList(occupations);
    }

    public List<TechnicalSkill> getTechnicalSkills() {
        return Collections.unmodifiableList(technicalSkills);
    }

    public List<WorkHistory> getWorkHistories() {
        return Collections.unmodifiableList(workHistories);
    }

    public boolean hasOccupations() {
        return !occupations.isEmpty();
    }

    public boolean hasSkills() {
        return !technicalSkills.isEmpty();
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof WorkerProfile profile)) {
            return false;
        }
        return id.equals(profile.id);
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }

    @Override
    public String toString() {
        return "WorkerProfile{id=" + id + ", fullName='" + fullName + "'}";
    }

    private static void requireNonBlank(String value, String errorMessage) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(errorMessage);
        }
    }
}
