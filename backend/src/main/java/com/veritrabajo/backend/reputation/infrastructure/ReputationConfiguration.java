package com.veritrabajo.backend.reputation.infrastructure;

import com.veritrabajo.backend.reputation.domain.service.ReputationCalculator;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ReputationConfiguration {

    @Bean
    public ReputationCalculator reputationCalculator() {
        return new ReputationCalculator();
    }
}
