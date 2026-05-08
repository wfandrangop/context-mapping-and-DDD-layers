package com.veritrabajo.backend.workerprofile.infrastructure.persistence;

import com.veritrabajo.backend.workerprofile.domain.model.Occupation;
import com.veritrabajo.backend.workerprofile.domain.model.Occupation.ExpertiseLevel;
import com.veritrabajo.backend.workerprofile.domain.model.RawDescription;
import com.veritrabajo.backend.workerprofile.domain.model.TechnicalSkill;
import com.veritrabajo.backend.workerprofile.domain.model.WorkerProfile;
import com.veritrabajo.backend.workerprofile.domain.repository.WorkerProfileRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * JPA-backed {@link WorkerProfileRepository} mapping aggregates to {@link WorkerProfileEntity}.
 */
@Repository
public class JpaWorkerProfileRepository implements WorkerProfileRepository {

    private static final String OCCUPATION_DELIMITER = "\\|";
    private static final int OCCUPATION_PARTS = 2;

    private final SpringWorkerProfileRepository springRepository;

    public JpaWorkerProfileRepository(
            SpringWorkerProfileRepository springRepository
    ) {
        this.springRepository = springRepository;
    }

    @Override
    public WorkerProfile save(WorkerProfile profile) {
        WorkerProfileEntity entity = toEntity(profile);
        WorkerProfileEntity saved = springRepository.save(entity);
        return toDomain(saved);
    }

    @Override
    public WorkerProfile findById(String id) {
        return springRepository.findById(id)
                .map(this::toDomain)
                .orElse(null);
    }

    @Override
    public boolean existsByPhoneNumber(String phoneNumber) {
        return springRepository.existsByPhoneNumber(phoneNumber);
    }

    private WorkerProfileEntity toEntity(WorkerProfile profile) {
        WorkerProfileEntity entity = new WorkerProfileEntity(
                profile.getId(),
                profile.getFullName(),
                profile.getPhoneNumber()
        );
        if (profile.getRawDescription() != null) {
            entity.setRawDescription(profile.getRawDescription().getText());
        }
        entity.setOccupations(serializeOccupations(profile));
        entity.setTechnicalSkills(serializeSkills(profile));
        return entity;
    }

    private List<String> serializeOccupations(WorkerProfile profile) {
        return profile.getOccupations()
                .stream()
                .map(o -> o.getTradeName() + "|" + o.getLevel().name())
                .toList();
    }

    private List<String> serializeSkills(WorkerProfile profile) {
        return profile.getTechnicalSkills()
                .stream()
                .map(TechnicalSkill::getSkillName)
                .toList();
    }

    private WorkerProfile toDomain(WorkerProfileEntity entity) {
        WorkerProfile profile = WorkerProfile.create(
                entity.getFullName(),
                entity.getPhoneNumber()
        );
        if (entity.getRawDescription() != null) {
            profile.assignRawDescription(
                    RawDescription.of(entity.getRawDescription())
            );
        }
        restoreOccupations(profile, entity.getOccupations());
        restoreSkills(profile, entity.getTechnicalSkills());
        return profile;
    }

    private void restoreOccupations(WorkerProfile profile, List<String> data) {
        for (String occupationData : data) {
            String[] parts = occupationData.split(OCCUPATION_DELIMITER);
            if (parts.length == OCCUPATION_PARTS) {
                ExpertiseLevel level = parseLevel(parts[1]);
                profile.addOccupation(Occupation.of(parts[0], level));
            }
        }
    }

    private void restoreSkills(WorkerProfile profile, List<String> skillNames) {
        for (String skillName : skillNames) {
            profile.addTechnicalSkill(TechnicalSkill.of(skillName));
        }
    }

    private ExpertiseLevel parseLevel(String levelStr) {
        try {
            return ExpertiseLevel.valueOf(levelStr);
        } catch (IllegalArgumentException e) {
            return ExpertiseLevel.INTERMEDIATE;
        }
    }
}
