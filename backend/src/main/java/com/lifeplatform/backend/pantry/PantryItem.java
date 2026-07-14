package com.lifeplatform.backend.pantry;

import com.lifeplatform.backend.catalog.MarketItem;
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
@Table(name = "pantry_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class PantryItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idPantryItem;

    private String ingredientName;
    private Double quantity;
    private String unit;

    @ManyToOne
    @JoinColumn(name = "id_market_item")
    private MarketItem marketItem;
}
