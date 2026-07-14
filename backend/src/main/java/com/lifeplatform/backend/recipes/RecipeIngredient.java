package com.lifeplatform.backend.recipes;

import com.lifeplatform.backend.catalog.MarketItem;
import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "recipe_ingredients")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class RecipeIngredient {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idRecipeIngredient;

    @ManyToOne
    @JoinColumn(name = "id_recipe", nullable = false)
    @JsonBackReference
    private Recipe recipe;

    @ManyToOne
    @JoinColumn(name = "id_market_item", nullable = false)
    private MarketItem marketItem;

    private Double quantityRequired;
    private String unitRequired;
}
