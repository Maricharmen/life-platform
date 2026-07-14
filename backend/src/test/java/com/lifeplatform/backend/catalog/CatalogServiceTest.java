package com.lifeplatform.backend.catalog;

import com.lifeplatform.backend.pantry.PantryItem;
import com.lifeplatform.backend.pantry.PantryRepository;
import com.lifeplatform.backend.recipes.Recipe;
import com.lifeplatform.backend.recipes.RecipeIngredient;
import com.lifeplatform.backend.recipes.RecipeRepository;
import com.lifeplatform.backend.shared.UnitConverter;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class CatalogServiceTest {

    @Mock
    private RecipeRepository recipeRepository;

    @Mock
    private PantryRepository pantryRepository;

    @Mock
    private SupermarketItemRepository supermarketItemRepository;

    private CatalogService catalogService;

    @BeforeEach
    void setUp() {
        catalogService = new CatalogService(recipeRepository, pantryRepository, supermarketItemRepository, new UnitConverter());
    }

    @Test
    void checkRecipeAvailabilityMarksCookableWhenPantryHasEnough() {
        MarketItem rice = marketItem(1L, "Arroz");
        Recipe recipe = recipe(10L, ingredient(rice, 500.0, "g"));

        PantryItem pantryEntry = pantryItem(rice, 1.0, "kg");

        when(recipeRepository.findById(10L)).thenReturn(Optional.of(recipe));
        when(pantryRepository.findByMarketItem(rice)).thenReturn(List.of(pantryEntry));

        RecipeAvailabilityDTO status = catalogService.checkRecipeAvailability(10L);

        assertThat(status.isCookable()).isTrue();
        assertThat(status.getMissingIngredients()).isEmpty();
    }

    @Test
    void checkRecipeAvailabilityReportsMathematicallyCorrectDeficitWhenNotEnough() {
        MarketItem rice = marketItem(1L, "Arroz");
        Recipe recipe = recipe(11L, ingredient(rice, 750.0, "g"));

        PantryItem pantryEntry = pantryItem(rice, 0.25, "kg");

        when(recipeRepository.findById(11L)).thenReturn(Optional.of(recipe));
        when(pantryRepository.findByMarketItem(rice)).thenReturn(List.of(pantryEntry));

        RecipeAvailabilityDTO status = catalogService.checkRecipeAvailability(11L);

        assertThat(status.isCookable()).isFalse();
        assertThat(status.getMissingIngredients()).hasSize(1);
        assertThat(status.getMissingIngredients().get(0).getDeficit()).isEqualTo(500.0);
        assertThat(status.getMissingIngredients().get(0).getUnit()).isEqualTo("g");
    }

    @Test
    void checkRecipeAvailabilityTreatsMissingPantryRowsAsZeroAvailable() {
        MarketItem oats = marketItem(2L, "Avena");
        Recipe recipe = recipe(12L, ingredient(oats, 300.0, "g"));

        when(recipeRepository.findById(12L)).thenReturn(Optional.of(recipe));
        when(pantryRepository.findByMarketItem(oats)).thenReturn(List.of());

        RecipeAvailabilityDTO status = catalogService.checkRecipeAvailability(12L);

        assertThat(status.isCookable()).isFalse();
        assertThat(status.getMissingIngredients()).hasSize(1);
        assertThat(status.getMissingIngredients().get(0).getDeficit()).isEqualTo(300.0);
    }

    @Test
    void calculateRecipeCostComputesExactCostByBaseUnit() {
        MarketItem rice = marketItem(3L, "Arroz");
        Recipe recipe = recipe(20L, ingredient(rice, 250.0, "g"));

        stubNoPriceForAll(rice.getIdMarketItem());
        when(supermarketItemRepository.findByMarketItem_IdMarketItemAndSupermarketName(3L, Supermarket.CHEDRAUI))
                .thenReturn(Optional.of(supermarketItem(rice, Supermarket.CHEDRAUI, 80.0, 1000.0, "g")));

        when(recipeRepository.findById(20L)).thenReturn(Optional.of(recipe));

        RecipeCostReportDTO report = catalogService.calculateRecipeCost(20L);

        assertThat(report.getSelectedSupermarket()).isEqualTo(Supermarket.CHEDRAUI);
        assertThat(report.getTotalCost()).isEqualTo(20.0);
        assertThat(report.getCostBySupermarket().get(Supermarket.CHEDRAUI)).isEqualTo(20.0);
    }

    @Test
    void calculateRecipeCostDoesNotSelectStoreWithIncompletePriceAsNaN() {
        MarketItem rice = marketItem(4L, "Arroz");
        MarketItem chicken = marketItem(5L, "Pollo");
        Recipe recipe = recipe(21L, ingredient(rice, 500.0, "g"), ingredient(chicken, 500.0, "g"));

        when(supermarketItemRepository.findByMarketItem_IdMarketItemAndSupermarketName(4L, Supermarket.CHEDRAUI))
                .thenReturn(Optional.of(supermarketItem(rice, Supermarket.CHEDRAUI, 50.0, 1000.0, "g")));
        // CHEDRAUI has no chicken price, so result must be NaN.

        when(supermarketItemRepository.findByMarketItem_IdMarketItemAndSupermarketName(5L, Supermarket.CHEDRAUI))
            .thenReturn(Optional.empty());

        when(supermarketItemRepository.findByMarketItem_IdMarketItemAndSupermarketName(4L, Supermarket.AMAZON))
            .thenReturn(Optional.empty());

        when(supermarketItemRepository.findByMarketItem_IdMarketItemAndSupermarketName(4L, Supermarket.SORIANA))
            .thenReturn(Optional.empty());

        when(supermarketItemRepository.findByMarketItem_IdMarketItemAndSupermarketName(4L, Supermarket.WALMART))
            .thenReturn(Optional.of(supermarketItem(rice, Supermarket.WALMART, 60.0, 1000.0, "g")));
        when(supermarketItemRepository.findByMarketItem_IdMarketItemAndSupermarketName(5L, Supermarket.WALMART))
            .thenReturn(Optional.of(supermarketItem(chicken, Supermarket.WALMART, 120.0, 1000.0, "g")));

        when(recipeRepository.findById(21L)).thenReturn(Optional.of(recipe));

        RecipeCostReportDTO report = catalogService.calculateRecipeCost(21L);

        assertThat(report.getCostBySupermarket().get(Supermarket.CHEDRAUI)).isNaN();
        assertThat(report.getSelectedSupermarket()).isEqualTo(Supermarket.WALMART);
    }

    @Test
    void calculateRecipeCostReturnsNullSelectedSupermarketWhenNoStoreHasAllPrices() {
        MarketItem rice = marketItem(6L, "Arroz");
        Recipe recipe = recipe(22L, ingredient(rice, 500.0, "g"));

        stubNoPriceForAll(rice.getIdMarketItem());
        when(recipeRepository.findById(22L)).thenReturn(Optional.of(recipe));

        RecipeCostReportDTO report = catalogService.calculateRecipeCost(22L);

        assertThat(report.getSelectedSupermarket()).isNull();
        assertThat(report.getTotalCost()).isNull();
        assertThat(report.getCostBySupermarket().values()).allMatch(value -> value.isNaN());
    }

    @Test
    void calculateShoppingPlanConsolidatesDemandForSameMarketItemAcrossRecipes() {
        MarketItem rice = marketItem(7L, "Arroz");
        Recipe recipe1 = recipe(30L, ingredient(rice, 600.0, "g"));
        Recipe recipe2 = recipe(31L, ingredient(rice, 900.0, "g"));

        when(recipeRepository.findById(30L)).thenReturn(Optional.of(recipe1));
        when(recipeRepository.findById(31L)).thenReturn(Optional.of(recipe2));
        when(pantryRepository.findByMarketItem(rice)).thenReturn(List.of(pantryItem(rice, 200.0, "g")));

        stubNoPriceForAll(rice.getIdMarketItem());
        when(supermarketItemRepository.findByMarketItem_IdMarketItemAndSupermarketName(7L, Supermarket.CHEDRAUI))
                .thenReturn(Optional.of(supermarketItem(rice, Supermarket.CHEDRAUI, 40.0, 1000.0, "g")));

        ShoppingPlanReportDTO report = catalogService.calculateShoppingPlan(List.of(30L, 31L));

        assertThat(report.getItemsToBuy()).hasSize(1);
        assertThat(report.getItemsToBuy().get(0).getIngredientName()).isEqualTo("Arroz");
        assertThat(report.getItemsToBuy().get(0).getQuantityToBuy()).isEqualTo(1300.0);
    }

    @Test
    void calculateShoppingPlanUsesWholePackagesWithCeilForCost() {
        MarketItem rice = marketItem(8L, "Arroz");
        Recipe recipe = recipe(32L, ingredient(rice, 1100.0, "g"));

        when(recipeRepository.findById(32L)).thenReturn(Optional.of(recipe));
        when(pantryRepository.findByMarketItem(rice)).thenReturn(List.of());

        stubNoPriceForAll(rice.getIdMarketItem());
        when(supermarketItemRepository.findByMarketItem_IdMarketItemAndSupermarketName(8L, Supermarket.CHEDRAUI))
                .thenReturn(Optional.of(supermarketItem(rice, Supermarket.CHEDRAUI, 35.0, 1000.0, "g")));

        ShoppingPlanReportDTO report = catalogService.calculateShoppingPlan(List.of(32L));

        // 1100g -> 2 packages of 1000g.
        assertThat(report.getTotalCostBySupermarket().get(Supermarket.CHEDRAUI)).isEqualTo(70.0);
    }

    @Test
    void calculateShoppingPlanSkipsIngredientsCoveredByPantryDelta() {
        MarketItem rice = marketItem(9L, "Arroz");
        Recipe recipe = recipe(33L, ingredient(rice, 300.0, "g"));

        when(recipeRepository.findById(33L)).thenReturn(Optional.of(recipe));
        when(pantryRepository.findByMarketItem(rice)).thenReturn(List.of(pantryItem(rice, 1.0, "kg")));
        ShoppingPlanReportDTO report = catalogService.calculateShoppingPlan(List.of(33L));

        assertThat(report.getItemsToBuy()).isEmpty();
    }

    private void stubNoPriceForAll(Long marketItemId) {
        when(supermarketItemRepository.findByMarketItem_IdMarketItemAndSupermarketName(marketItemId, Supermarket.AMAZON))
                .thenReturn(Optional.empty());
        when(supermarketItemRepository.findByMarketItem_IdMarketItemAndSupermarketName(marketItemId, Supermarket.CHEDRAUI))
                .thenReturn(Optional.empty());
        when(supermarketItemRepository.findByMarketItem_IdMarketItemAndSupermarketName(marketItemId, Supermarket.WALMART))
                .thenReturn(Optional.empty());
        when(supermarketItemRepository.findByMarketItem_IdMarketItemAndSupermarketName(marketItemId, Supermarket.SORIANA))
                .thenReturn(Optional.empty());
    }

    private Recipe recipe(Long id, RecipeIngredient... ingredients) {
        Recipe recipe = new Recipe();
        recipe.setIdRecipe(id);
        recipe.setTitle("R" + id);
        recipe.setIngredients(List.of(ingredients));
        return recipe;
    }

    private MarketItem marketItem(Long id, String standardName) {
        MarketItem marketItem = new MarketItem();
        marketItem.setIdMarketItem(id);
        marketItem.setStandardName(standardName);
        return marketItem;
    }

    private RecipeIngredient ingredient(MarketItem marketItem, Double qty, String unit) {
        RecipeIngredient ingredient = new RecipeIngredient();
        ingredient.setMarketItem(marketItem);
        ingredient.setQuantityRequired(qty);
        ingredient.setUnitRequired(unit);
        return ingredient;
    }

    private PantryItem pantryItem(MarketItem marketItem, Double qty, String unit) {
        PantryItem pantryItem = new PantryItem();
        pantryItem.setMarketItem(marketItem);
        pantryItem.setQuantity(qty);
        pantryItem.setUnit(unit);
        return pantryItem;
    }

    private SupermarketItem supermarketItem(MarketItem marketItem,
                                            Supermarket supermarket,
                                            Double packagePrice,
                                            Double packageSize,
                                            String unit) {
        SupermarketItem supermarketItem = new SupermarketItem();
        supermarketItem.setMarketItem(marketItem);
        supermarketItem.setSupermarketName(supermarket);
        supermarketItem.setPackagePrice(packagePrice);
        supermarketItem.setPackageSize(packageSize);
        supermarketItem.setUnit(unit);
        return supermarketItem;
    }
}
