package com.lifeplatform.backend.catalog;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecipeAvailabilityDTO {

    private boolean cookable;
    private List<MissingIngredientDTO> missingIngredients;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class MissingIngredientDTO {
        private String name;
        private Double deficit;
        private String unit;
    }
}
