package com.lifeplatform.backend.catalog;

import java.util.Optional;

public interface AiMatchingAgent {
    /**
     * Toma el texto libre de un ingrediente y devuelve su equivalente estandarizado.
     */
    // TODO: Conectar con Spring AI (OpenAI/Gemini) en producción.
    Optional<String> standardizeIngredient(String rawIngredientName);
}
