package com.lifeplatform.backend.catalog;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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
}
