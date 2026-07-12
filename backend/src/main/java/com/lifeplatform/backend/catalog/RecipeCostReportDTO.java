package com.lifeplatform.backend.catalog;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecipeCostReportDTO {

    private Long recipeId;
    private String recipeTitle;
    private Supermarket selectedSupermarket;
    private Double totalCost;
    private Map<Supermarket, Double> costBySupermarket;
    private List<IngredientCostDTO> ingredientCosts;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class IngredientCostDTO {
        private String ingredientName;
        private Double quantityRequired;
        private String unit;
        private Map<Supermarket, Double> costBySupermarket;
    }
}
