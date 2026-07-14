package com.lifeplatform.backend.shared;

import org.springframework.stereotype.Component;

@Component
public class UnitConverter {

    public double toBaseUnit(Double quantity, String unit) {
        if (quantity == null || quantity <= 0.0) {
            return 0.0;
        }

        if (unit == null) {
            return quantity;
        }

        String normalizedUnit = unit.trim().toLowerCase();
        return switch (normalizedUnit) {
            case "g", "gr", "gram", "grams", "ml" -> quantity / 1000.0;
            case "kg", "l", "lt" -> quantity;
            default -> quantity;
        };
    }

    public double fromBaseUnit(double quantityBase, String unit) {
        if (quantityBase <= 0.0) {
            return 0.0;
        }
        if (unit == null) {
            return quantityBase;
        }

        String normalizedUnit = unit.trim().toLowerCase();
        return switch (normalizedUnit) {
            case "g", "gr", "gram", "grams", "ml" -> quantityBase * 1000.0;
            case "kg", "l", "lt" -> quantityBase;
            default -> quantityBase;
        };
    }
}
