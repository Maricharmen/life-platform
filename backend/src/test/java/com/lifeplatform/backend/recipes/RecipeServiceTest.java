package com.lifeplatform.backend.recipes;

import com.lifeplatform.backend.catalog.MarketItem;
import com.lifeplatform.backend.catalog.MarketItemResolver;
import com.lifeplatform.backend.shared.ResourceNotFoundException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class RecipeServiceTest {

    @Mock
    private RecipeRepository recipeRepository;

    @Mock
    private MarketItemResolver marketItemResolver;

    private RecipeService recipeService;

    @BeforeEach
    void setUp() {
        recipeService = new RecipeService(recipeRepository, marketItemResolver);
    }

    @Test
    void createRecipeBuildsIngredientsAndLinksBidirectionalRelationship() {
        IngredientRequestDTO riceDto = new IngredientRequestDTO("Arroz", 300.0, "g");
        IngredientRequestDTO chickenDto = new IngredientRequestDTO("Pollo", 500.0, "g");
        CreateRecipeRequestDTO request = new CreateRecipeRequestDTO("Bowl", "Mezclar", 20, List.of(riceDto, chickenDto));

        MarketItem rice = new MarketItem();
        rice.setIdMarketItem(1L);
        rice.setStandardName("Arroz");

        MarketItem chicken = new MarketItem();
        chicken.setIdMarketItem(2L);
        chicken.setStandardName("Pollo");

        when(marketItemResolver.resolve("Arroz")).thenReturn(rice);
        when(marketItemResolver.resolve("Pollo")).thenReturn(chicken);
        when(recipeRepository.save(any(Recipe.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Recipe result = recipeService.createRecipe(request);

        assertThat(result.getIngredients()).hasSize(2);
        assertThat(result.getIngredients().get(0).getMarketItem()).isSameAs(rice);
        assertThat(result.getIngredients().get(1).getMarketItem()).isSameAs(chicken);
        assertThat(result.getIngredients().get(0).getRecipe()).isSameAs(result);
        assertThat(result.getIngredients().get(1).getRecipe()).isSameAs(result);
        verify(marketItemResolver).resolve("Arroz");
        verify(marketItemResolver).resolve("Pollo");
    }

    @Test
    void createRecipeWithNullIngredientsDoesNotThrowAndSavesWithoutIngredients() {
        CreateRecipeRequestDTO request = new CreateRecipeRequestDTO("Simple", "Hervir", 5, null);
        when(recipeRepository.save(any(Recipe.class))).thenAnswer(invocation -> invocation.getArgument(0));

        assertThatCode(() -> recipeService.createRecipe(request)).doesNotThrowAnyException();

        ArgumentCaptor<Recipe> captor = ArgumentCaptor.forClass(Recipe.class);
        verify(recipeRepository).save(captor.capture());
        assertThat(captor.getValue().getIngredients()).isEmpty();
    }

    @Test
    void createRecipeWithEmptyIngredientsDoesNotThrowAndSavesWithoutIngredients() {
        CreateRecipeRequestDTO request = new CreateRecipeRequestDTO("Simple", "Hervir", 5, List.of());
        when(recipeRepository.save(any(Recipe.class))).thenAnswer(invocation -> invocation.getArgument(0));

        Recipe result = recipeService.createRecipe(request);

        assertThat(result.getIngredients()).isEmpty();
    }

    @Test
    void updateRecipeDetailsDoesNotAlterExistingIngredientList() {
        Recipe existing = new Recipe();
        existing.setIdRecipe(7L);
        existing.setTitle("Antes");
        existing.setInstruction("Antes");
        existing.setPreparationTime(10);

        RecipeIngredient ingredient = new RecipeIngredient();
        ingredient.setIdRecipeIngredient(100L);
        ingredient.setRecipe(existing);
        existing.setIngredients(new ArrayList<>(List.of(ingredient)));

        when(recipeRepository.findById(7L)).thenReturn(Optional.of(existing));
        when(recipeRepository.save(existing)).thenReturn(existing);

        UpdateRecipeDetailsRequestDTO request = new UpdateRecipeDetailsRequestDTO("Despues", "Nueva", 30);
        Recipe result = recipeService.updateRecipeDetails(7L, request);

        assertThat(result.getTitle()).isEqualTo("Despues");
        assertThat(result.getInstruction()).isEqualTo("Nueva");
        assertThat(result.getPreparationTime()).isEqualTo(30);
        assertThat(result.getIngredients()).hasSize(1);
        assertThat(result.getIngredients().get(0).getIdRecipeIngredient()).isEqualTo(100L);
    }

    @Test
    void updateIngredientQuantityFailsIfIngredientIdExistsInAnotherRecipeButNotInTarget() {
        Recipe targetRecipe = new Recipe();
        targetRecipe.setIdRecipe(1L);

        RecipeIngredient otherRecipeIngredient = new RecipeIngredient();
        otherRecipeIngredient.setIdRecipeIngredient(99L);

        Recipe otherRecipe = new Recipe();
        otherRecipe.setIdRecipe(2L);
        otherRecipe.setIngredients(new ArrayList<>(List.of(otherRecipeIngredient)));

        RecipeIngredient targetIngredient = new RecipeIngredient();
        targetIngredient.setIdRecipeIngredient(10L);
        targetRecipe.setIngredients(new ArrayList<>(List.of(targetIngredient)));

        when(recipeRepository.findById(1L)).thenReturn(Optional.of(targetRecipe));

        UpdateRecipeIngredientRequestDTO request = new UpdateRecipeIngredientRequestDTO(400.0, "g");

        assertThatThrownBy(() -> recipeService.updateIngredientQuantity(1L, 99L, request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("El ingrediente no existe en esta receta");
    }

    @Test
    void removeIngredientFromRecipeFailsWhenIngredientDoesNotBelongToRecipe() {
        Recipe recipe = new Recipe();
        recipe.setIdRecipe(1L);

        RecipeIngredient ingredient = new RecipeIngredient();
        ingredient.setIdRecipeIngredient(10L);
        recipe.setIngredients(new ArrayList<>(List.of(ingredient)));

        when(recipeRepository.findById(1L)).thenReturn(Optional.of(recipe));

        assertThatThrownBy(() -> recipeService.removeIngredientFromRecipe(1L, 999L))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("El ingrediente no existe en esta receta");
    }
}
