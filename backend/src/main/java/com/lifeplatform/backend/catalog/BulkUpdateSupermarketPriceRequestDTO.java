package com.lifeplatform.backend.catalog;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class BulkUpdateSupermarketPriceRequestDTO {
    @NotBlank(message = "Debes indicar el nombre del ingrediente")
    private String ingredientName;

    @NotNull(message = "El supermercado es obligatorio")
    private Supermarket supermarketName;

    @NotNull(message = "El precio del paquete es obligatorio")
    @Positive(message = "El precio del paquete debe ser mayor a cero")
    private Double packagePrice;

    @NotNull(message = "El tamaño del paquete es obligatorio")
    @Positive(message = "El tamaño del paquete debe ser mayor a cero")
    private Double packageSize;

    @NotBlank(message = "La unidad es obligatoria")
    private String unit;
}