package com.veritrabajo.backend.workerprofile.infrastructure.acl;

import org.springframework.boot.context.properties.ConfigurationProperties;

@ConfigurationProperties(prefix = "spring.ai.openai.chat.options")
public record GroqProperties(String model) {}
