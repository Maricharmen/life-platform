package com.lifeplatform.backend.recipes;

import com.lifeplatform.backend.catalog.MarketItem;
import com.lifeplatform.backend.catalog.MarketItemRepository;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class RecipeIngredientPersistenceTest {

    @Autowired
    private RecipeRepository recipeRepository;

    @Autowired
    private MarketItemRepository marketItemRepository;

    @Autowired
    private EntityManager entityManager;

    @Test
    void savingRecipeWithIngredientsPersistsChildrenByCascade() {
        MarketItem rice = marketItemRepository.save(new MarketItem(null, "Arroz", "Granos"));
        MarketItem chicken = marketItemRepository.save(new MarketItem(null, "Pollo", "Proteina"));

        Recipe recipe = new Recipe();
        recipe.setTitle("Bowl");
        recipe.setInstruction("Mezclar");
        recipe.setPreparationTime(20);

        RecipeIngredient i1 = new RecipeIngredient();
        i1.setMarketItem(rice);
        i1.setQuantityRequired(300.0);
        i1.setUnitRequired("g");
        i1.setRecipe(recipe);

        RecipeIngredient i2 = new RecipeIngredient();
        i2.setMarketItem(chicken);
        i2.setQuantityRequired(500.0);
        i2.setUnitRequired("g");
        i2.setRecipe(recipe);

        recipe.setIngredients(new ArrayList<>(List.of(i1, i2)));

        Recipe saved = recipeRepository.saveAndFlush(recipe);

        assertThat(saved.getIdRecipe()).isNotNull();
        assertThat(saved.getIngredients()).hasSize(2);
        assertThat(saved.getIngredients().get(0).getIdRecipeIngredient()).isNotNull();
        assertThat(saved.getIngredients().get(1).getIdRecipeIngredient()).isNotNull();
    }

    @Test
    void removingIngredientAndSavingDeletesOrphanByOrphanRemoval() {
        MarketItem rice = marketItemRepository.save(new MarketItem(null, "Arroz", "Granos"));
        MarketItem chicken = marketItemRepository.save(new MarketItem(null, "Pollo", "Proteina"));

        Recipe recipe = new Recipe();
        recipe.setTitle("Bowl");
        recipe.setInstruction("Mezclar");
        recipe.setPreparationTime(20);

        RecipeIngredient i1 = new RecipeIngredient();
        i1.setMarketItem(rice);
        i1.setQuantityRequired(300.0);
        i1.setUnitRequired("g");
        i1.setRecipe(recipe);

        RecipeIngredient i2 = new RecipeIngredient();
        i2.setMarketItem(chicken);
        i2.setQuantityRequired(500.0);
        i2.setUnitRequired("g");
        i2.setRecipe(recipe);

        recipe.setIngredients(new ArrayList<>(List.of(i1, i2)));
        Recipe saved = recipeRepository.saveAndFlush(recipe);

        saved.getIngredients().remove(0);
        recipeRepository.saveAndFlush(saved);
        entityManager.clear();

        Recipe reloaded = recipeRepository.findById(saved.getIdRecipe()).orElseThrow();
        assertThat(reloaded.getIngredients()).hasSize(1);
    }

    @Test
    void savingRecipeIngredientWithoutSettingRecipeFailsBecauseIdRecipeIsNonNull() {
        MarketItem rice = marketItemRepository.save(new MarketItem(null, "Arroz", "Granos"));

        Recipe recipe = new Recipe();
        recipe.setTitle("Bowl");
        recipe.setInstruction("Mezclar");
        recipe.setPreparationTime(20);

        RecipeIngredient ingredientWithoutBackReference = new RecipeIngredient();
        ingredientWithoutBackReference.setMarketItem(rice);
        ingredientWithoutBackReference.setQuantityRequired(300.0);
        ingredientWithoutBackReference.setUnitRequired("g");
        // Intentionally do not call ingredientWithoutBackReference.setRecipe(recipe)
        // to document that the manual bidirectional link is mandatory.

        recipe.setIngredients(new ArrayList<>(List.of(ingredientWithoutBackReference)));

        assertThatThrownBy(() -> recipeRepository.saveAndFlush(recipe))
                .isInstanceOfAny(JpaSystemException.class, PersistenceException.class, RuntimeException.class);
    }
}
