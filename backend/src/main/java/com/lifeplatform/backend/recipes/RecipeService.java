package com.lifeplatform.backend.recipes;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class RecipeService {
    @Autowired 
    private RecipeRepository recipeRepository;

    public List<Recipe> getAllRecipes(){
        return recipeRepository.findAll();
    }

    public Recipe saveRecipe(Recipe recipe) {
        if (recipe.getPreparationTimeMinutes() != null && recipe.getPreparationTimeMinutes() <= 0) {
            throw new IllegalArgumentException("El tiempo de preparación debe ser mayor a 0 minutos");
        }

        if (recipe.getIngredients() != null) {
            for (RecipeIngredient ingredient : recipe.getIngredients()) {
                ingredient.setRecipe(recipe); // Le decimos al ingrediente a qué receta pertenece
            }
        }
        
        return recipeRepository.save(recipe);
    }

}
