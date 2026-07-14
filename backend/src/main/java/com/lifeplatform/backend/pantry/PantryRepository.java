package com.lifeplatform.backend.pantry;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import com.lifeplatform.backend.catalog.MarketItem;

import java.util.List;
import java.util.Optional;

@Repository
public interface PantryRepository extends JpaRepository<PantryItem, Long> {
    Optional<PantryItem> findByIngredientNameIgnoreCase(String ingredientName);

    List<PantryItem> findByMarketItem(MarketItem marketItem);

    Optional<PantryItem> findByMarketItem_IdMarketItem(Long idMarketItem);
}
