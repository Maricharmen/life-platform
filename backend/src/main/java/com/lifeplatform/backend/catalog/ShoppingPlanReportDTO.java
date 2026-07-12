package com.lifeplatform.backend.catalog;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.Map;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ShoppingPlanReportDTO {

    private List<ShoppingItemDTO> itemsToBuy;
    private Map<Supermarket, Double> totalCostBySupermarket;
    private Supermarket cheapestSupermarket;

    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class ShoppingItemDTO {
        private String ingredientName;
        private Double quantityToBuy;
        private String unit;
        private Double packagePrice;
        private Integer packagesNeeded;
        private Double estimatedCost;
    }
}
