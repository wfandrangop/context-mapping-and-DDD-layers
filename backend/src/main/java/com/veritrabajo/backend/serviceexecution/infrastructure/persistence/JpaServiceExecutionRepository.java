package com.veritrabajo.backend.serviceexecution.infrastructure.persistence;

import com.veritrabajo.backend.serviceexecution.domain.model.ServiceExecution;
import com.veritrabajo.backend.serviceexecution.domain.port.ServiceExecutionRepository;
import com.veritrabajo.backend.serviceexecution.infrastructure.persistence.entity.ServiceExecutionEntity;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JpaServiceExecutionRepository implements ServiceExecutionRepository {

    private final SpringDataServiceExecutionRepository jpaRepository;

    public JpaServiceExecutionRepository(SpringDataServiceExecutionRepository jpaRepository) {
        this.jpaRepository = jpaRepository;
    }

    @Override
    public Optional<ServiceExecution> findById(UUID id) {
        return jpaRepository.findById(id).map(ServiceExecutionMapper::toDomain);
    }

    @Override
    public ServiceExecution save(ServiceExecution execution) {
        ServiceExecutionEntity entity = jpaRepository.findById(execution.id())
                .orElseGet(ServiceExecutionEntity::new);
        ServiceExecutionMapper.updateEntity(entity, execution);
        jpaRepository.save(entity);
        return execution;
    }

    @Override
    public List<ServiceExecution> findByWorkerId(String workerId) {
        return jpaRepository.findByWorkerId(workerId).stream()
                .map(ServiceExecutionMapper::toDomain)
                .toList();
    }
}
