package com.veritrabajo.backend.workerprofile.domain.model;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

/**
 * WorkerProfile aggregate root: all mutations go through these methods.
 */
public final class WorkerProfile {

    private final String id;
    private final String fullName;
    private final String phoneNumber;
    private final List<Occupation> occupations;
    private final List<TechnicalSkill> technicalSkills;
    private final List<WorkHistory> workHistories;
    private RawDescription rawDescription;
    private OwnedTools ownedTools;

    private WorkerProfile(String fullName, String phoneNumber) {
        this.id = UUID.randomUUID().toString();
        this.fullName = fullName;
        this.phoneNumber = phoneNumber;
        this.ownedTools = OwnedTools.empty();
        this.occupations = new ArrayList<>();
        this.technicalSkills = new ArrayList<>();
        this.workHistories = new ArrayList<>();
    }

    /**
     * Factory entry used by
     * {@link com.veritrabajo.backend.workerprofile.domain.factory.ProfileFactory}.
     */
    public static WorkerProfile create(String fullName, String phoneNumber) {
        requireNonBlank(fullName, "Full name cannot be blank");
        requireNonBlank(phoneNumber, "Phone number cannot be blank");
        return new WorkerProfile(fullName.trim(), phoneNumber.trim());
    }

    /** Assigns raw description text exactly once. */
    public void assignRawDescription(RawDescription description) {
        if (this.rawDescription != null) {
            throw new IllegalStateException(
                    "Raw description was already assigned to this profile"
            );
        }
        this.rawDescription = description;
    }

    /** Adds an occupation; rejects nulls and duplicates. */
    public void addOccupation(Occupation occupation) {
        if (occupation == null) {
            throw new IllegalArgumentException(
                    "Occupation cannot be null"
            );
        }
        if (occupations.contains(occupation)) {
            throw new IllegalArgumentException(
                    "Occupation already present on profile: " + occupation.getTradeName()
            );
        }
        occupations.add(occupation);
    }

    /** Adds a technical skill inferred externally; ignores duplicates. */
    public void addTechnicalSkill(TechnicalSkill skill) {
        if (skill == null) {
            throw new IllegalArgumentException(
                    "Technical skill cannot be null"
            );
        }
        if (technicalSkills.contains(skill)) {
            return;
        }
        technicalSkills.add(skill);
    }

    public void addWorkHistory(WorkHistory history) {
        if (history == null) {
            throw new IllegalArgumentException(
                    "Work history cannot be null"
            );
        }
        workHistories.add(history);
    }

    public void updateOwnedTools(OwnedTools tools) {
        if (tools == null) {
            throw new IllegalArgumentException(
                    "Owned tools cannot be null"
            );
        }
        this.ownedTools = tools;
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
        return "WorkerProfile{id='" + id + "', fullName='" + fullName + "'}";
    }

    private static void requireNonBlank(String value, String errorMessage) {
        if (value == null || value.isBlank()) {
            throw new IllegalArgumentException(errorMessage);
        }
    }
}
