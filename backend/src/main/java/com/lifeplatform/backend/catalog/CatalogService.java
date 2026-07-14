package com.lifeplatform.backend.catalog;

import com.lifeplatform.backend.pantry.PantryItem;
import com.lifeplatform.backend.pantry.PantryRepository;
import com.lifeplatform.backend.recipes.Recipe;
import com.lifeplatform.backend.recipes.RecipeIngredient;
import com.lifeplatform.backend.recipes.RecipeRepository;
import com.lifeplatform.backend.shared.ResourceNotFoundException;
import com.lifeplatform.backend.shared.UnitConverter;
import org.springframework.stereotype.Service;

import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.ArrayList;
import java.util.EnumMap;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.*;
import java.util.stream.Collectors;

@Service
public class CatalogService {

    private final RecipeRepository recipeRepository;
    private final PantryRepository pantryRepository;
    private final SupermarketItemRepository supermarketItemRepository;
    private final UnitConverter unitConverter;

    public CatalogService(RecipeRepository recipeRepository,
                          PantryRepository pantryRepository,
                          SupermarketItemRepository supermarketItemRepository,
                          UnitConverter unitConverter) {
        this.recipeRepository = recipeRepository;
        this.pantryRepository = pantryRepository;
        this.supermarketItemRepository = supermarketItemRepository;
        this.unitConverter = unitConverter;
    }

    public RecipeAvailabilityDTO checkRecipeAvailability(Long recipeId) {
        Recipe recipe = getRecipeOrThrow(recipeId);

        List<RecipeAvailabilityDTO.MissingIngredientDTO> missingIngredients = new ArrayList<>();
        for (RecipeIngredient ingredient : recipe.getIngredients()) {
            MarketItem marketItem = ingredient.getMarketItem();
            if (marketItem == null) {
                continue;
            }

            List<PantryItem> pantryItems = pantryRepository.findByMarketItem(marketItem);
            double availableBase = pantryItems.stream()
                    .mapToDouble(item -> unitConverter.toBaseUnit(item.getQuantity(), item.getUnit()))
                    .sum();
                double requiredBase = unitConverter.toBaseUnit(ingredient.getQuantityRequired(), ingredient.getUnitRequired());

            if (availableBase < requiredBase) {
                double deficitBase = requiredBase - availableBase;
                missingIngredients.add(new RecipeAvailabilityDTO.MissingIngredientDTO(
                        marketItem.getStandardName(),
                        round(unitConverter.fromBaseUnit(deficitBase, ingredient.getUnitRequired())),
                        ingredient.getUnitRequired()
                ));
            }
        }

        return new RecipeAvailabilityDTO(missingIngredients.isEmpty(), missingIngredients);
    }

    public RecipeCostReportDTO calculateRecipeCost(Long recipeId) {
        Recipe recipe = getRecipeOrThrow(recipeId);

        Map<Supermarket, Double> costBySupermarket = new EnumMap<>(Supermarket.class);

        for (Supermarket supermarket : Supermarket.values()) {
            costBySupermarket.put(supermarket, round(calculateExactCostForSupermarket(recipe, supermarket)));
        }

        Supermarket selectedSupermarket = findCheapestSupermarket(costBySupermarket);
        Double totalCost = selectedSupermarket == null ? null : costBySupermarket.get(selectedSupermarket);

        return new RecipeCostReportDTO(
                recipe.getIdRecipe(),
                recipe.getTitle(),
                selectedSupermarket,
                totalCost,
                costBySupermarket
        );
    }

    public ShoppingPlanReportDTO calculateShoppingPlan(List<Long> recipeIds) {
        Map<Long, DemandAccumulator> demandByMarketItem = new LinkedHashMap<>();

        for (Long recipeId : recipeIds) {
            Recipe recipe = getRecipeOrThrow(recipeId);
            for (RecipeIngredient ingredient : recipe.getIngredients()) {
                MarketItem marketItem = ingredient.getMarketItem();
                if (marketItem == null || marketItem.getIdMarketItem() == null) {
                    continue;
                }

                double requiredBase = unitConverter.toBaseUnit(ingredient.getQuantityRequired(), ingredient.getUnitRequired());
                demandByMarketItem.compute(marketItem.getIdMarketItem(), (id, current) -> {
                    DemandAccumulator next = current == null
                            ? new DemandAccumulator(marketItem, ingredient.getUnitRequired(), 0.0)
                            : current;
                    next.requiredBase += requiredBase;
                    return next;
                });
            }
        }

        List<ShoppingPlanReportDTO.ShoppingItemDTO> itemsToBuy = new ArrayList<>();
        Map<Long, Double> deltasByMarketItem = new HashMap<>();

        for (Map.Entry<Long, DemandAccumulator> entry : demandByMarketItem.entrySet()) {
            DemandAccumulator demand = entry.getValue();
            List<PantryItem> pantryItems = pantryRepository.findByMarketItem(demand.marketItem);
            double availableBase = pantryItems.stream()
                    .mapToDouble(item -> unitConverter.toBaseUnit(item.getQuantity(), item.getUnit()))
                    .sum();

            double deltaBase = Math.max(0.0, demand.requiredBase - availableBase);
            if (deltaBase <= 0.0) {
                continue;
            }

            deltasByMarketItem.put(entry.getKey(), deltaBase);
            itemsToBuy.add(new ShoppingPlanReportDTO.ShoppingItemDTO(
                    demand.marketItem.getStandardName(),
                    round(unitConverter.fromBaseUnit(deltaBase, demand.displayUnit)),
                    demand.displayUnit
            ));
        }

        Map<Supermarket, Double> totalCostBySupermarket = new EnumMap<>(Supermarket.class);
        for (Supermarket supermarket : Supermarket.values()) {
            totalCostBySupermarket.put(supermarket, round(calculatePackageCartCost(deltasByMarketItem, supermarket)));
        }

        Supermarket cheapestSupermarket = findCheapestSupermarket(totalCostBySupermarket);
        Double cheapestCost = cheapestSupermarket == null ? null : totalCostBySupermarket.get(cheapestSupermarket);

        return new ShoppingPlanReportDTO(
                itemsToBuy,
                totalCostBySupermarket,
                cheapestSupermarket,
                cheapestCost
        );
    }

    public List<Recipe> getAvailableRecipes() {
        return recipeRepository.findAll().stream()
                .filter(recipe -> checkRecipeAvailability(recipe.getIdRecipe()).isCookable())
                .collect(Collectors.toList());
    }

    public List<SupermarketItem> bulkUpdatePrices(List<SupermarketItem> prices) {
        if (prices == null || prices.isEmpty()) {
            return List.of();
        }
        return supermarketItemRepository.saveAll(prices);
    }

    private Recipe getRecipeOrThrow(Long recipeId) {
        return recipeRepository.findById(recipeId)
                .orElseThrow(() -> new ResourceNotFoundException("La receta no existe"));
    }

    private double calculateExactCostForSupermarket(Recipe recipe, Supermarket supermarket) {
        double total = 0.0;
        for (RecipeIngredient ingredient : recipe.getIngredients()) {
            MarketItem marketItem = ingredient.getMarketItem();
            if (marketItem == null || marketItem.getIdMarketItem() == null) {
                return Double.NaN;
            }

            Optional<SupermarketItem> supermarketItemOpt = supermarketItemRepository
                    .findByMarketItem_IdMarketItemAndSupermarketName(marketItem.getIdMarketItem(), supermarket);
            if (supermarketItemOpt.isEmpty()) {
                return Double.NaN;
            }

            SupermarketItem supermarketItem = supermarketItemOpt.get();
            double requiredBase = unitConverter.toBaseUnit(ingredient.getQuantityRequired(), ingredient.getUnitRequired());
            double packageSizeBase = unitConverter.toBaseUnit(supermarketItem.getPackageSize(), supermarketItem.getUnit());

            if (packageSizeBase <= 0.0 || supermarketItem.getPackagePrice() == null) {
                return Double.NaN;
            }
            double unitPriceBase = supermarketItem.getPackagePrice() / packageSizeBase;
            total += requiredBase * unitPriceBase;
        }

        return total;
    }

    private double calculatePackageCartCost(Map<Long, Double> deltasByMarketItem, Supermarket supermarket) {
        double total = 0.0;
        for (Map.Entry<Long, Double> entry : deltasByMarketItem.entrySet()) {
            Optional<SupermarketItem> supermarketItemOpt = supermarketItemRepository
                    .findByMarketItem_IdMarketItemAndSupermarketName(entry.getKey(), supermarket);
            if (supermarketItemOpt.isEmpty()) {
                return Double.NaN;
            }

            SupermarketItem supermarketItem = supermarketItemOpt.get();
            double packageSizeBase = unitConverter.toBaseUnit(supermarketItem.getPackageSize(), supermarketItem.getUnit());
            if (packageSizeBase <= 0.0 || supermarketItem.getPackagePrice() == null) {
                return Double.NaN;
            }

            double packagesNeeded = Math.ceil(entry.getValue() / packageSizeBase);
            total += packagesNeeded * supermarketItem.getPackagePrice();
        }

        return total;
    }

    private Supermarket findCheapestSupermarket(Map<Supermarket, Double> costBySupermarket) {
        return costBySupermarket.entrySet().stream()
                .filter(entry -> entry.getValue() != null && !entry.getValue().isNaN())
                .min(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(null);
    }

    private Double round(Double value) {
        if (value == null || value.isNaN()) {
            return value;
        }
        return BigDecimal.valueOf(value).setScale(4, RoundingMode.HALF_UP).doubleValue();
    }

    private static class DemandAccumulator {
        private final MarketItem marketItem;
        private final String displayUnit;
        private double requiredBase;

        private DemandAccumulator(MarketItem marketItem, String displayUnit, double requiredBase) {
            this.marketItem = marketItem;
            this.displayUnit = displayUnit;
            this.requiredBase = requiredBase;
        }
    }
}