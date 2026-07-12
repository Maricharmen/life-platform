package com.lifeplatform.backend.catalog;

import com.lifeplatform.backend.pantry.PantryItem;
import com.lifeplatform.backend.pantry.PantryRepository;
import com.lifeplatform.backend.recipes.Recipe;
import com.lifeplatform.backend.recipes.RecipeIngredient;
import com.lifeplatform.backend.recipes.RecipeRepository;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CatalogServiceTest {

    @Mock
    private RecipeRepository recipeRepository;

    @Mock
    private PantryRepository pantryRepository;

    @Mock
    private MarketItemRepository marketItemRepository;

    @Mock
    private SupermarketItemRepository supermarketItemRepository;

    @InjectMocks
    private CatalogService catalogService;

    @Test
    void checkRecipeAvailabilityReturnsMissingIngredientsWhenPantryIsShort() {
        Recipe recipe = new Recipe();
        recipe.setIdRecipe(10L);

        MarketItem marketItem = new MarketItem();
        marketItem.setIdMarketItem(100L);
        marketItem.setStandardName("Avena");

        RecipeIngredient ingredient = new RecipeIngredient();
        ingredient.setMarketItem(marketItem);
        ingredient.setQuantityRequired(300.0);
        ingredient.setUnitRequired("g");
        recipe.setIngredients(List.of(ingredient));

        PantryItem pantryItem = new PantryItem();
        pantryItem.setMarketItem(marketItem);
        pantryItem.setQuantity(100.0);
        pantryItem.setUnit("g");

        when(recipeRepository.findById(10L)).thenReturn(Optional.of(recipe));
        when(pantryRepository.findByMarketItem(marketItem)).thenReturn(List.of(pantryItem));

        RecipeAvailabilityDTO status = catalogService.checkRecipeAvailability(10L);

        assertFalse(status.isCookable());
        assertEquals(1, status.getMissingIngredients().size());
        assertEquals(200.0, status.getMissingIngredients().get(0).getDeficit());
    }

    @Test
    void calculateRecipeCostReturnsCheapestSupermarket() {
        Recipe recipe = new Recipe();
        recipe.setIdRecipe(20L);

        MarketItem marketItem = new MarketItem();
        marketItem.setIdMarketItem(200L);
        marketItem.setStandardName("Avena");

        RecipeIngredient ingredient = new RecipeIngredient();
        ingredient.setMarketItem(marketItem);
        ingredient.setQuantityRequired(250.0);
        ingredient.setUnitRequired("g");
        recipe.setIngredients(List.of(ingredient));

        when(recipeRepository.findById(20L)).thenReturn(Optional.of(recipe));
        when(supermarketItemRepository.findByMarketItemId(200L)).thenReturn(List.of(
                createSupermarketItem(Supermarket.WALMART, 100.0, 1000.0, "g", 0.1),
                createSupermarketItem(Supermarket.CHEDRAUI, 90.0, 1000.0, "g", 0.09),
                createSupermarketItem(Supermarket.SORIANA, 95.0, 1000.0, "g", 0.095),
                createSupermarketItem(Supermarket.AMAZON, 110.0, 1000.0, "g", 0.11)
        ));

        RecipeCostReportDTO report = catalogService.calculateRecipeCost(20L);

        assertEquals(Supermarket.CHEDRAUI, report.getSelectedSupermarket());
        assertEquals(22.5, report.getTotalCost());
    }

    @Test
    void calculateShoppingPlanUsesPackagePricesAndChoosesCheapestSupermarket() {
        Recipe recipe = new Recipe();
        recipe.setIdRecipe(30L);

        MarketItem marketItem = new MarketItem();
        marketItem.setIdMarketItem(300L);
        marketItem.setStandardName("Arroz");

        RecipeIngredient ingredient = new RecipeIngredient();
        ingredient.setMarketItem(marketItem);
        ingredient.setQuantityRequired(1500.0);
        ingredient.setUnitRequired("g");
        recipe.setIngredients(List.of(ingredient));

        PantryItem pantryItem = new PantryItem();
        pantryItem.setMarketItem(marketItem);
        pantryItem.setQuantity(500.0);
        pantryItem.setUnit("g");

        when(recipeRepository.findById(30L)).thenReturn(Optional.of(recipe));
        when(pantryRepository.findByMarketItem(marketItem)).thenReturn(List.of(pantryItem));
        when(supermarketItemRepository.findByMarketItemId(300L)).thenReturn(List.of(
                createSupermarketItem(Supermarket.WALMART, 50.0, 1000.0, "g", 0.05),
                createSupermarketItem(Supermarket.CHEDRAUI, 40.0, 1000.0, "g", 0.04),
                createSupermarketItem(Supermarket.SORIANA, 45.0, 1000.0, "g", 0.045),
                createSupermarketItem(Supermarket.AMAZON, 55.0, 1000.0, "g", 0.055)
        ));

        ShoppingPlanReportDTO report = catalogService.calculateShoppingPlan(List.of(30L));

        assertEquals(Supermarket.CHEDRAUI, report.getCheapestSupermarket());
        assertEquals(1, report.getItemsToBuy().size());
        assertEquals(1000.0, report.getItemsToBuy().get(0).getQuantityToBuy());
        assertEquals(40.0, report.getTotalCostBySupermarket().get(Supermarket.CHEDRAUI));
    }

    private SupermarketItem createSupermarketItem(Supermarket supermarket, double packagePrice, double packageSize, String unit, double unitPrice) {
        SupermarketItem item = new SupermarketItem();
        item.setSupermarketName(supermarket);
        item.setPackagePrice(packagePrice);
        item.setPackageSize(packageSize);
        item.setUnit(unit);
        item.setUnitPrice(unitPrice);
        return item;
    }
}
