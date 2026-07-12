package com.lifeplatform.backend.recipes;

import com.lifeplatform.backend.catalog.MarketItem;
import com.lifeplatform.backend.catalog.RecipeAvailabilityDTO;
import com.lifeplatform.backend.pantry.PantryItem;
import com.lifeplatform.backend.pantry.PantryRepository;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class RecipeService {
    private final RecipeRepository recipeRepository;
    private final PantryRepository pantryRepository;

    public RecipeService(RecipeRepository recipeRepository, PantryRepository pantryRepository) {
        this.recipeRepository = recipeRepository;
        this.pantryRepository = pantryRepository;
    }

    public List<Recipe> getAllRecipes(){
        return recipeRepository.findAll();
    }

    public Optional<Recipe> getById(Long id) {
        return recipeRepository.findById(id);
    }

    public Recipe saveRecipe(Recipe recipe) {
        if (recipe.getPreparationTime() != null && recipe.getPreparationTime() <= 0) {
            throw new IllegalArgumentException("El tiempo de preparación debe ser mayor a 0 minutos");
        }

        if (recipe.getIngredients() != null) {
            for (RecipeIngredient ingredient : recipe.getIngredients()) {
                ingredient.setRecipe(recipe);
            }
        }

        return recipeRepository.save(recipe);
    }

    public Recipe updateRecipe(Long id, Recipe updatedRecipe) {
        Recipe existingRecipe = recipeRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("La receta no existe"));
        existingRecipe.setTitle(updatedRecipe.getTitle());
        existingRecipe.setInstruction(updatedRecipe.getInstruction());
        existingRecipe.setPreparationTime(updatedRecipe.getPreparationTime());
        existingRecipe.setIngredients(updatedRecipe.getIngredients());
        return recipeRepository.save(existingRecipe);
    }

    public void deleteRecipe(Long id) {
        recipeRepository.deleteById(id);
    }

    public RecipeStatusDTO checkAvailability(Long recipeId) {
        Recipe recipe = recipeRepository.findById(recipeId)
                .orElseThrow(() -> new IllegalArgumentException("La receta no existe"));

        RecipeStatusDTO status = new RecipeStatusDTO();
        List<RecipeStatusDTO.MissingIngredientInfo> missingList = new ArrayList<>();
        boolean canCook = true;

        for (RecipeIngredient reqIngredient : recipe.getIngredients()) {
            MarketItem marketItem = reqIngredient.getMarketItem();
            if (marketItem == null) {
                continue;
            }

            List<PantryItem> pantryItems = pantryRepository.findByMarketItem(marketItem);
            double available = pantryItems.stream().mapToDouble(PantryItem::getQuantity).sum();
            double required = Optional.ofNullable(reqIngredient.getQuantityRequired()).orElse(0.0);

            if (available < required) {
                canCook = false;
                RecipeStatusDTO.MissingIngredientInfo missing = new RecipeStatusDTO.MissingIngredientInfo();
                missing.setName(marketItem.getStandardName());
                missing.setQuantityNeeded(required - available);
                missing.setUnit(reqIngredient.getUnitRequired());
                missingList.add(missing);
            }
        }

        status.setCanCook(canCook);
        status.setMissingIngredients(missingList);
        status.setMessage(canCook
                ? "¡Tienes todo listo en tu despensa para cocinar este platillo!"
                : "Te hacen falta ingredientes para completar esta receta.");
        return status;
    }

}
