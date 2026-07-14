package com.lifeplatform.backend.recipes;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UpdateRecipeIngredientRequestDTO {
    @NotNull(message = "La cantidad requerida es obligatoria")
    @Positive(message = "La cantidad requerida debe ser mayor a cero")
    private Double quantityRequired;

    @NotBlank(message = "La unidad requerida es obligatoria")
    private String unitRequired;
}
