package com.lifeplatform.backend.catalog;

import java.util.Optional;

public interface AiMatchingAgent {
    /**
     * Takes free-text ingredient input and returns its standardized equivalent.
     */
    // TODO: Integrate with Spring AI (OpenAI/Gemini) in production.
    Optional<String> standardizeIngredient(String rawName);
}
