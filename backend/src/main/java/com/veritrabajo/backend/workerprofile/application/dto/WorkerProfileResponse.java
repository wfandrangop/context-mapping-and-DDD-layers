package com.veritrabajo.backend.workerprofile.application.dto;

import com.veritrabajo.backend.workerprofile.domain.model.Occupation;
import com.veritrabajo.backend.workerprofile.domain.model.TechnicalSkill;
import com.veritrabajo.backend.workerprofile.domain.model.WorkerProfile;
import java.util.List;

public record WorkerProfileResponse(
        String id,
        String fullName,
        String phoneNumber,
        List<String> occupations,
        List<String> technicalSkills,
        int reputationScore
) {
    public static WorkerProfileResponse from(WorkerProfile profile, int reputationScore) {
        return new WorkerProfileResponse(
                profile.getId().asString(),
                profile.getFullName(),
                profile.getPhoneNumber(),
                profile.getOccupations().stream().map(Occupation::getTradeName).toList(),
                profile.getTechnicalSkills().stream().map(TechnicalSkill::getSkillName).toList(),
                reputationScore
        );
    }
}
