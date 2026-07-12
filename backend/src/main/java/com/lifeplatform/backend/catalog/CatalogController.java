package com.lifeplatform.backend.catalog;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/catalog")
public class CatalogController {

    private final CatalogService catalogService;

    public CatalogController(CatalogService catalogService) {
        this.catalogService = catalogService;
    }

    @GetMapping("/recipe-status/{recipeId}")
    public ResponseEntity<RecipeAvailabilityDTO> getRecipeAvailability(@PathVariable Long recipeId) {
        return new ResponseEntity<>(catalogService.checkRecipeAvailability(recipeId), HttpStatus.OK);
    }

    @GetMapping("/recipe-cost/{recipeId}")
    public ResponseEntity<RecipeCostReportDTO> getRecipeCost(@PathVariable Long recipeId) {
        return new ResponseEntity<>(catalogService.calculateRecipeCost(recipeId), HttpStatus.OK);
    }

    @GetMapping("/shopping-plan")
    public ResponseEntity<ShoppingPlanReportDTO> getShoppingPlan(@RequestParam List<Long> recipeIds) {
        return new ResponseEntity<>(catalogService.calculateShoppingPlan(recipeIds), HttpStatus.OK);
    }

    @PostMapping("/prices/bulk-update")
    public ResponseEntity<List<SupermarketItem>> bulkUpdatePrices(@RequestBody List<SupermarketItem> prices) {
        return new ResponseEntity<>(catalogService.bulkUpdatePrices(prices), HttpStatus.OK);
    }
}
