package com.lifeplatform.backend.catalog;

import com.lifeplatform.backend.pantry.PantryItem;
import com.lifeplatform.backend.pantry.PantryRepository;
import com.lifeplatform.backend.recipes.Recipe;
import com.lifeplatform.backend.recipes.RecipeIngredient;
import com.lifeplatform.backend.recipes.RecipeRepository;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class CatalogService {

    private final RecipeRepository recipeRepository;
    private final PantryRepository pantryRepository;
    private final MarketItemRepository marketItemRepository;
    private final SupermarketItemRepository supermarketItemRepository;

    public CatalogService(RecipeRepository recipeRepository,
                          PantryRepository pantryRepository,
                          MarketItemRepository marketItemRepository,
                          SupermarketItemRepository supermarketItemRepository) {
        this.recipeRepository = recipeRepository;
        this.pantryRepository = pantryRepository;
        this.marketItemRepository = marketItemRepository;
        this.supermarketItemRepository = supermarketItemRepository;
    }

    public RecipeAvailabilityDTO checkRecipeAvailability(Long recipeId) {
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new IllegalArgumentException("La receta no existe"));

        List<RecipeAvailabilityDTO.MissingIngredientDTO> missingIngredients = new ArrayList<>();
        for (RecipeIngredient ingredient : Optional.ofNullable(recipe.getIngredients()).orElse(List.of())) {
            MarketItem marketItem = ingredient.getMarketItem();
            if (marketItem == null) {
                continue;
            }

            List<PantryItem> pantryItems = pantryRepository.findByMarketItem(marketItem);
            double available = pantryItems.stream().mapToDouble(PantryItem::getQuantity).sum();
            double required = Optional.ofNullable(ingredient.getQuantityRequired()).orElse(0.0);
            if (available < required) {
                missingIngredients.add(new RecipeAvailabilityDTO.MissingIngredientDTO(
                        marketItem.getStandardName(),
                        required - available,
                        ingredient.getUnitRequired()
                ));
            }
        }

        return new RecipeAvailabilityDTO(missingIngredients.isEmpty(), missingIngredients);
    }

    public RecipeCostReportDTO calculateRecipeCost(Long recipeId) {
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new IllegalArgumentException("La receta no existe"));

        Map<Supermarket, Double> costBySupermarket = new EnumMap<>(Supermarket.class);
        List<RecipeCostReportDTO.IngredientCostDTO> ingredientCosts = new ArrayList<>();
        for (Supermarket supermarket : Supermarket.values()) {
            costBySupermarket.put(supermarket, 0.0);
        }

        for (RecipeIngredient ingredient : Optional.ofNullable(recipe.getIngredients()).orElse(List.of())) {
            MarketItem marketItem = ingredient.getMarketItem();
            if (marketItem == null) {
                continue;
            }

            double quantityRequired = Optional.ofNullable(ingredient.getQuantityRequired()).orElse(0.0);
            String unitRequired = Optional.ofNullable(ingredient.getUnitRequired()).orElse("unit");

            Map<Supermarket, Double> currentCostBySupermarket = new EnumMap<>(Supermarket.class);
            for (Supermarket supermarket : Supermarket.values()) {
                SupermarketItem supermarketItem = findSupermarketItem(marketItem, supermarket);
                double exactCost = calculateExactUnitCost(quantityRequired, unitRequired, supermarketItem);
                currentCostBySupermarket.put(supermarket, exactCost);
                costBySupermarket.put(supermarket, costBySupermarket.get(supermarket) + exactCost);
            }

            ingredientCosts.add(new RecipeCostReportDTO.IngredientCostDTO(
                    marketItem.getStandardName(),
                    quantityRequired,
                    unitRequired,
                    currentCostBySupermarket
            ));
        }

        Supermarket selectedSupermarket = costBySupermarket.entrySet().stream()
                .filter(entry -> entry.getValue() > 0)
                .min(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(Supermarket.WALMART);

        return new RecipeCostReportDTO(
                recipe.getIdRecipe(),
                recipe.getTitle(),
                selectedSupermarket,
                costBySupermarket.get(selectedSupermarket),
                costBySupermarket,
                ingredientCosts
        );
    }

    public ShoppingPlanReportDTO calculateShoppingPlan(List<Long> recipeIds) {
        Map<MarketItem, Double> requiredByItem = new LinkedHashMap<>();
        for (Long recipeId : recipeIds) {
            Recipe recipe = recipeRepository.findById(recipeId).orElse(null);
            if (recipe == null) {
                continue;
            }
            for (RecipeIngredient ingredient : Optional.ofNullable(recipe.getIngredients()).orElse(List.of())) {
                MarketItem marketItem = ingredient.getMarketItem();
                if (marketItem == null) {
                    continue;
                }
                requiredByItem.merge(marketItem, Optional.ofNullable(ingredient.getQuantityRequired()).orElse(0.0), Double::sum);
            }
        }

        List<ShoppingPlanReportDTO.ShoppingItemDTO> itemsToBuy = new ArrayList<>();
        for (Map.Entry<MarketItem, Double> entry : requiredByItem.entrySet()) {
            MarketItem marketItem = entry.getKey();
            double required = entry.getValue();
            List<PantryItem> pantryItems = pantryRepository.findByMarketItem(marketItem);
            double available = pantryItems.stream().mapToDouble(PantryItem::getQuantity).sum();
            double delta = Math.max(0.0, required - available);
            if (delta <= 0) {
                continue;
            }

            itemsToBuy.add(new ShoppingPlanReportDTO.ShoppingItemDTO(
                    marketItem.getStandardName(),
                    delta,
                    "g",
                    null,
                    0,
                    0.0
            ));
        }

        Map<Supermarket, Double> totals = new EnumMap<>(Supermarket.class);
        for (Supermarket supermarket : Supermarket.values()) {
            totals.put(supermarket, 0.0);
        }

        for (ShoppingPlanReportDTO.ShoppingItemDTO item : itemsToBuy) {
            for (Supermarket supermarket : Supermarket.values()) {
                MarketItem marketItem = marketItemRepository.findByStandardNameIgnoreCase(item.getIngredientName()).orElse(null);
                if (marketItem == null) {
                    continue;
                }
                SupermarketItem supermarketItem = findSupermarketItem(marketItem, supermarket);
                if (supermarketItem == null) {
                    continue;
                }
                double normalizedQuantity = normalizeQuantityToBaseUnit(item.getQuantityToBuy(), item.getUnit());
                double normalizedPackageSize = normalizeQuantityToBaseUnit(supermarketItem.getPackageSize(), supermarketItem.getUnit());
                double packagesNeeded = normalizedPackageSize > 0 ? Math.ceil(normalizedQuantity / normalizedPackageSize) : 0.0;
                double cost = packagesNeeded * supermarketItem.getPackagePrice();
                totals.put(supermarket, totals.get(supermarket) + cost);
            }
        }

        Supermarket cheapestSupermarket = totals.entrySet().stream()
                .filter(entry -> entry.getValue() > 0)
                .min(Map.Entry.comparingByValue())
                .map(Map.Entry::getKey)
                .orElse(Supermarket.WALMART);

        return new ShoppingPlanReportDTO(itemsToBuy, totals, cheapestSupermarket);
    }

    public List<SupermarketItem> bulkUpdatePrices(List<SupermarketItem> prices) {
        return prices == null || prices.isEmpty() ? List.of() : supermarketItemRepository.saveAll(prices);
    }

    private SupermarketItem findSupermarketItem(MarketItem marketItem, Supermarket supermarket) {
        return supermarketItemRepository.findByMarketItemId(marketItem.getIdMarketItem()).stream()
                .filter(item -> item.getSupermarketName() == supermarket)
                .findFirst()
                .orElse(null);
    }

    private double calculateExactUnitCost(double quantityRequired, String unitRequired, SupermarketItem supermarketItem) {
        if (supermarketItem == null || supermarketItem.getUnitPrice() == null || quantityRequired <= 0) {
            return 0.0;
        }
        double normalizedQuantity = normalizeQuantityToBaseUnit(quantityRequired, unitRequired);
        return normalizedQuantity * supermarketItem.getUnitPrice();
    }

    private double normalizeQuantityToBaseUnit(double quantity, String unit) {
        if (quantity <= 0) {
            return 0.0;
        }
        if (unit == null) {
            return quantity;
        }
        String normalizedUnit = unit.trim().toLowerCase();
        return switch (normalizedUnit) {
            case "g", "ml" -> quantity / 1000.0;
            case "kg", "l" -> quantity;
            default -> quantity;
        };
    }
}