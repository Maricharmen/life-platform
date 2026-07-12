package com.lifeplatform.backend.recipes;

import lombok.Data;
import java.util.List;

@Data
public class RecipeStatusDTO {
    private boolean canCook;
    private String message;
    private List<MissingIngredientInfo> missingIngredients;

    @Data
    public static class MissingIngredientInfo {
        private String name;
        private Double quantityNeeded;
        private String unit;
    }
}
