package com.veritrabajo.backend.workerprofile.domain.model;

public final class Occupation {

    public enum ExpertiseLevel {
        BEGINNER, INTERMEDIATE, ADVANCED, EXPERT
    }

    private final String tradeName;
    private final ExpertiseLevel level;

    private Occupation(String tradeName, ExpertiseLevel level) {
        this.tradeName = tradeName;
        this.level = level;
    }

    public static Occupation of(String tradeName, ExpertiseLevel level) {
        if (tradeName == null || tradeName.isBlank()) {
            throw new IllegalArgumentException(
                    "Trade name cannot be blank"
            );
        }
        if (level == null) {
            throw new IllegalArgumentException(
                    "Expertise level cannot be null"
            );
        }
        return new Occupation(tradeName.trim(), level);
    }

    public String getTradeName() {
        return tradeName;
    }

    public ExpertiseLevel getLevel() {
        return level;
    }

    @Override
    public boolean equals(Object other) {
        if (this == other) {
            return true;
        }
        if (!(other instanceof Occupation that)) {
            return false;
        }
        return tradeName.equals(that.tradeName) && level == that.level;
    }

    @Override
    public int hashCode() {
        return 31 * tradeName.hashCode() + level.hashCode();
    }

    @Override
    public String toString() {
        return "Occupation{tradeName='" + tradeName + "', level=" + level + "}";
    }
}
