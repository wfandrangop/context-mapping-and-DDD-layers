package com.veritrabajo.backend.workerprofile.domain.model;

import java.util.Collections;
import java.util.List;

/**
 * Structured outcome of AI analysis: inferred occupations and technical skills.
 */
public final class AnalysisResult {

    private final List<Occupation> occupations;
    private final List<TechnicalSkill> technicalSkills;

    private AnalysisResult(
            List<Occupation> occupations,
            List<TechnicalSkill> technicalSkills
    ) {
        this.occupations = Collections.unmodifiableList(occupations);
        this.technicalSkills = Collections.unmodifiableList(technicalSkills);
    }

    public static AnalysisResult of(
            List<Occupation> occupations,
            List<TechnicalSkill> technicalSkills
    ) {
        if (occupations == null) {
            throw new IllegalArgumentException(
                    "Occupations list cannot be null"
            );
        }
        if (technicalSkills == null) {
            throw new IllegalArgumentException(
                    "Technical skills list cannot be null"
            );
        }
        return new AnalysisResult(occupations, technicalSkills);
    }

    public List<Occupation> getOccupations() {
        return occupations;
    }

    public List<TechnicalSkill> getTechnicalSkills() {
        return technicalSkills;
    }

    public boolean hasResults() {
        return !occupations.isEmpty() || !technicalSkills.isEmpty();
    }

    @Override
    public String toString() {
        return "AnalysisResult{occupations=" + occupations
                + ", technicalSkills=" + technicalSkills + "}";
    }
}
