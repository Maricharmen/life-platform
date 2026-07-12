package com.lifeplatform.backend.catalog;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.PrePersist;
import jakarta.persistence.PreUpdate;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Table(name = "supermarket_items")
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SupermarketItem {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long idSupermarketItem;

    @ManyToOne
    @JoinColumn(name = "id_market_item", nullable = false)
    private MarketItem marketItem;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Supermarket supermarketName;

    @Column(nullable = false)
    private Double packagePrice;

    @Column(nullable = false)
    private Double packageSize;

    @Column(nullable = false)
    private String unit;

    @Column(nullable = false)
    private Double unitPrice;

    @PrePersist
    @PreUpdate
    public void calculateUnitPrice() {
        if (packagePrice != null && packageSize != null && packageSize > 0) {
            this.unitPrice = packagePrice / packageSize;
        } else {
            this.unitPrice = 0.0;
        }
    }
}
