package com.veritrabajo.backend.workerprofile.domain.model;

/**
 * Single technical skill inferred from unstructured text (e.g. copper soldering).
 */
public final class TechnicalSkill {

    private final String skillName;

    private TechnicalSkill(String skillName) {
        this.skillName = skillName;
    }

    public static TechnicalSkill of(String skillName) {
        if (skillName == null || skillName.isBlank()) {
            throw new IllegalArgumentException(
                    "Skill name cannot be blank"
            );
        }
        return new TechnicalSkill(skillName.trim());
    }

    public String getSkillName() {
        return skillName;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof TechnicalSkill that)) {
            return false;
        }
        return skillName.equalsIgnoreCase(that.skillName);
    }

    @Override
    public int hashCode() {
        return skillName.toLowerCase().hashCode();
    }

    @Override
    public String toString() {
        return "TechnicalSkill{skillName='" + skillName + "'}";
    }
}
