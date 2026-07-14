package com.lifeplatform.backend.catalog;

import org.springframework.stereotype.Service;

import java.util.Optional;

@Service
public class MockAiMatchingAgent implements AiMatchingAgent {

    @Override
    public Optional<String> standardizeIngredient(String rawName) {
        if (rawName == null || rawName.isBlank()) {
            return Optional.empty();
        }

        String normalized = rawName.trim().toLowerCase();
        return switch (normalized) {
            case "avena", "avena integral" -> Optional.of("Avena");
            case "arroz" -> Optional.of("Arroz");
            case "pollo", "pechuga" -> Optional.of("Pollo");
            default -> Optional.of(rawName.trim());
        };
    }
}