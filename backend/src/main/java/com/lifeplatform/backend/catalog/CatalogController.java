package com.lifeplatform.backend.catalog;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/catalog")
public class CatalogController {

    private final CatalogService catalogService;
    private final PriceUpdateService priceUpdateService;

    public CatalogController(CatalogService catalogService, PriceUpdateService priceUpdateService) {
        this.catalogService = catalogService;
        this.priceUpdateService = priceUpdateService;
    }

    @GetMapping("/recipe-availability/{recipeId}")
    public ResponseEntity<RecipeAvailabilityDTO> getRecipeAvailability(@PathVariable Long recipeId) {
        return new ResponseEntity<>(catalogService.checkRecipeAvailability(recipeId), HttpStatus.OK);
    }

    @GetMapping("/recipe-cost/{recipeId}")
    public ResponseEntity<RecipeCostReportDTO> getRecipeCost(@PathVariable Long recipeId) {
        return new ResponseEntity<>(catalogService.calculateRecipeCost(recipeId), HttpStatus.OK);
    }

    @PostMapping("/shopping-plan")
    public ResponseEntity<ShoppingPlanReportDTO> getShoppingPlan(@RequestBody List<Long> recipeIds) {
        return new ResponseEntity<>(catalogService.calculateShoppingPlan(recipeIds), HttpStatus.OK);
    }

    @PutMapping("/prices/bulk-update")
    public ResponseEntity<List<SupermarketItem>> bulkUpdatePrices(@Valid @RequestBody List<BulkUpdateSupermarketPriceRequestDTO> prices) {
        return new ResponseEntity<>(priceUpdateService.updatePrices(prices), HttpStatus.OK);
    }
}
