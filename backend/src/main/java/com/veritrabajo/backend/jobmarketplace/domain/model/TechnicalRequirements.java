package com.veritrabajo.backend.jobmarketplace.domain.model;

import java.util.Collection;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;

public final class TechnicalRequirements {

    private final List<String> skills;

    private TechnicalRequirements(List<String> skills) {
        this.skills = List.copyOf(skills);
    }

    public static TechnicalRequirements of(Collection<String> sourceSkills) {
        Objects.requireNonNull(sourceSkills, "Technical requirements are required");
        Set<String> normalized = normalizeSkills(sourceSkills);
        validateNotEmpty(normalized);
        return new TechnicalRequirements(List.copyOf(normalized));
    }

    public List<String> skills() {
        return skills;
    }

    private static Set<String> normalizeSkills(Collection<String> sourceSkills) {
        Set<String> normalized = new LinkedHashSet<>();
        for (String sourceSkill : sourceSkills) {
            String skill = normalizeSkill(sourceSkill);
            if (!skill.isEmpty()) {
                normalized.add(skill);
            }
        }
        return normalized;
    }

    private static String normalizeSkill(String sourceSkill) {
        if (sourceSkill == null) {
            return "";
        }
        return sourceSkill.trim();
    }

    private static void validateNotEmpty(Set<String> normalized) {
        if (normalized.isEmpty()) {
            throw new IllegalArgumentException("At least one technical requirement is required");
        }
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof TechnicalRequirements that)) {
            return false;
        }
        return skills.equals(that.skills);
    }

    @Override
    public int hashCode() {
        return skills.hashCode();
    }
}
