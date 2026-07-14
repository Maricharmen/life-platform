package com.lifeplatform.backend.pantry;

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
public class UpdatePantryItemRequestDTO {
    @NotNull(message = "La cantidad es obligatoria")
    @Positive(message = "La cantidad debe ser mayor a cero")
    private Double quantity;

    @NotBlank(message = "La unidad es obligatoria")
    private String unit;
}
