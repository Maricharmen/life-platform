package com.lifeplatform.backend.recipes;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class IngredientRequestDTO {
    @NotBlank(message = "Debes indicar el nombre del ingrediente")
    private String ingredientName;

    @NotNull(message = "La cantidad requerida es obligatoria")
    @Positive(message = "La cantidad requerida debe ser mayor a cero")
    private Double quantityRequired;

    @NotBlank(message = "La unidad requerida es obligatoria")
    private String unitRequired;
}