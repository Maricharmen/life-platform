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
public class UpdateRecipeDetailsRequestDTO {
    @NotBlank(message = "El título de la receta es obligatorio")
    private String title;

    @NotBlank(message = "La instrucción de la receta es obligatoria")
    private String instruction;

    @NotNull(message = "El tiempo de preparación es obligatorio")
    @Positive(message = "El tiempo de preparación debe ser mayor a cero")
    private Integer preparationTime;
}
