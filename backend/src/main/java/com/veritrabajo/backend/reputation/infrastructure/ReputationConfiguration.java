package com.veritrabajo.backend.reputation.infrastructure;

import com.veritrabajo.backend.reputation.domain.service.ReputationCalculator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Spring configuration for the reputation bounded context.
 * Wires infrastructure adapters and domain service implementations
 * that should not carry Spring annotations in the domain layer.
 */
@Configuration
public class ReputationConfiguration {

    /**
     * Provides the {@link ReputationCalculator} domain service implementation.
     * Kept as a {@code @Bean} rather than annotating the domain class with
     * {@code @Component} to preserve domain layer independence from Spring.
     *
     * @return the reputation calculator instance
     */
    @Bean
    public ReputationCalculator reputationCalculator() {
        return new ReputationCalculator();
    }
}
