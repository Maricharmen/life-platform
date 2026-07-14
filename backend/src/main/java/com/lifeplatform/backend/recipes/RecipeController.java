package com.lifeplatform.backend.recipes;

import com.lifeplatform.backend.catalog.CatalogService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/recipes")
public class RecipeController {

    private final RecipeService recipeService;
    private final CatalogService catalogService;

    public RecipeController(RecipeService recipeService, CatalogService catalogService) {
        this.recipeService = recipeService;
        this.catalogService = catalogService;
    }

    @GetMapping
    public ResponseEntity<List<Recipe>> getAllRecipes() {
        List<Recipe> recipes = recipeService.getAllRecipes();
        return new ResponseEntity<>(recipes, HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Recipe> getRecipeById(@PathVariable Long id) {
        return recipeService.getById(id)
                .map(recipe -> new ResponseEntity<>(recipe, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @GetMapping("/available")
    public ResponseEntity<List<Recipe>> getAvailableRecipes() {
        return new ResponseEntity<>(catalogService.getAvailableRecipes(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<Recipe> createRecipe(@Valid @RequestBody CreateRecipeRequestDTO request) {
        Recipe savedRecipe = recipeService.createRecipe(request);
        return new ResponseEntity<>(savedRecipe, HttpStatus.CREATED);
    }

    @PutMapping("/{id}")
    public ResponseEntity<Recipe> updateRecipe(@PathVariable Long id, @Valid @RequestBody UpdateRecipeRequestDTO request) {
        Recipe updatedRecipe = recipeService.updateRecipe(id, request);
        return new ResponseEntity<>(updatedRecipe, HttpStatus.OK);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Recipe> updateRecipeDetails(@PathVariable Long id,
                                                      @Valid @RequestBody UpdateRecipeDetailsRequestDTO request) {
        Recipe updatedRecipe = recipeService.updateRecipeDetails(id, request);
        return new ResponseEntity<>(updatedRecipe, HttpStatus.OK);
    }

    @PutMapping("/{recipeId}/ingredients/{ingredientId}")
    public ResponseEntity<Recipe> updateIngredient(@PathVariable Long recipeId,
                                                   @PathVariable Long ingredientId,
                                                   @Valid @RequestBody UpdateRecipeIngredientRequestDTO request) {
        Recipe updatedRecipe = recipeService.updateIngredientQuantity(recipeId, ingredientId, request);
        return new ResponseEntity<>(updatedRecipe, HttpStatus.OK);
    }

    @PostMapping("/{recipeId}/ingredients")
    public ResponseEntity<Recipe> addIngredient(@PathVariable Long recipeId,
                                                @Valid @RequestBody IngredientRequestDTO request) {
        Recipe updatedRecipe = recipeService.addIngredientToRecipe(recipeId, request);
        return new ResponseEntity<>(updatedRecipe, HttpStatus.CREATED);
    }

    @DeleteMapping("/{recipeId}/ingredients/{ingredientId}")
    public ResponseEntity<Recipe> removeIngredient(@PathVariable Long recipeId,
                                                   @PathVariable Long ingredientId) {
        Recipe updatedRecipe = recipeService.removeIngredientFromRecipe(recipeId, ingredientId);
        return new ResponseEntity<>(updatedRecipe, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteRecipe(@PathVariable Long id) {
        recipeService.deleteRecipe(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}
