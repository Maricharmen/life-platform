package com.lifeplatform.backend.recipes;

import com.lifeplatform.backend.catalog.MarketItem;
import com.lifeplatform.backend.catalog.MarketItemResolver;
import com.lifeplatform.backend.shared.ResourceNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Optional;

@Service
public class RecipeService {
    private final RecipeRepository recipeRepository;
    private final MarketItemResolver marketItemResolver;

    public RecipeService(RecipeRepository recipeRepository,
                         MarketItemResolver marketItemResolver) {
        this.recipeRepository = recipeRepository;
        this.marketItemResolver = marketItemResolver;
    }

    public List<Recipe> getAllRecipes() {
        return recipeRepository.findAll();
    }

    public Optional<Recipe> getById(Long id) {
        return recipeRepository.findById(id);
    }

    @Transactional
    public Recipe createRecipe(CreateRecipeRequestDTO request) {
        Recipe recipe = new Recipe();
        recipe.setTitle(request.getTitle());
        recipe.setInstruction(request.getInstruction());
        recipe.setPreparationTime(request.getPreparationTime());
        recipe.setIngredients(new ArrayList<>());

        List<RecipeIngredient> ingredients = new ArrayList<>();
        for (IngredientRequestDTO dto : request.getIngredients() == null ? List.<IngredientRequestDTO>of() : request.getIngredients()) {
            ingredients.add(toRecipeIngredient(dto, recipe));
        }
        recipe.setIngredients(ingredients);

        return recipeRepository.save(recipe);
    }

    @Transactional
    public Recipe updateRecipe(Long id, UpdateRecipeRequestDTO request) {
        Recipe existingRecipe = recipeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("La receta no existe"));

        existingRecipe.setTitle(request.getTitle());
        existingRecipe.setInstruction(request.getInstruction());
        existingRecipe.setPreparationTime(request.getPreparationTime());

        existingRecipe.getIngredients().clear();
        List<RecipeIngredient> rebuiltIngredients = new ArrayList<>();
        for (IngredientRequestDTO dto : request.getIngredients() == null ? List.<IngredientRequestDTO>of() : request.getIngredients()) {
            rebuiltIngredients.add(toRecipeIngredient(dto, existingRecipe));
        }
        existingRecipe.getIngredients().addAll(rebuiltIngredients);

        return recipeRepository.save(existingRecipe);
    }

    @Transactional
    public Recipe updateRecipeDetails(Long id, UpdateRecipeDetailsRequestDTO request) {
        Recipe recipe = recipeRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("La receta no existe"));

        recipe.setTitle(request.getTitle());
        recipe.setInstruction(request.getInstruction());
        recipe.setPreparationTime(request.getPreparationTime());

        return recipeRepository.save(recipe);
    }

    @Transactional
    public Recipe updateIngredientQuantity(Long recipeId, Long ingredientId, UpdateRecipeIngredientRequestDTO request) {
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new ResourceNotFoundException("La receta no existe"));

        RecipeIngredient ingredient = recipe.getIngredients().stream()
            .filter(item -> Objects.equals(item.getIdRecipeIngredient(), ingredientId))
                .findFirst()
                .orElseThrow(() -> new ResourceNotFoundException("El ingrediente no existe en esta receta"));

        ingredient.setQuantityRequired(request.getQuantityRequired());
        ingredient.setUnitRequired(request.getUnitRequired());

        return recipeRepository.save(recipe);
    }

    @Transactional
    public Recipe addIngredientToRecipe(Long recipeId, IngredientRequestDTO request) {
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new ResourceNotFoundException("La receta no existe"));

        recipe.getIngredients().add(toRecipeIngredient(request, recipe));

        return recipeRepository.save(recipe);
    }

    @Transactional
    public Recipe removeIngredientFromRecipe(Long recipeId, Long ingredientId) {
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new ResourceNotFoundException("La receta no existe"));

        boolean removed = recipe.getIngredients().removeIf(item -> Objects.equals(item.getIdRecipeIngredient(), ingredientId));
        if (!removed) {
            throw new ResourceNotFoundException("El ingrediente no existe en esta receta");
        }

        return recipeRepository.save(recipe);
    }

    public void deleteRecipe(Long id) {
        recipeRepository.deleteById(id);
    }

    private RecipeIngredient toRecipeIngredient(IngredientRequestDTO dto, Recipe recipe) {
        MarketItem marketItem = marketItemResolver.resolve(dto.getIngredientName());

        RecipeIngredient ingredient = new RecipeIngredient();
        ingredient.setMarketItem(marketItem);
        ingredient.setQuantityRequired(dto.getQuantityRequired());
        ingredient.setUnitRequired(dto.getUnitRequired());
        ingredient.setRecipe(recipe);
        return ingredient;
    }
}