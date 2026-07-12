package com.lifeplatform.backend.catalog;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface SupermarketItemRepository extends JpaRepository<SupermarketItem, Long> {
    List<SupermarketItem> findByMarketItemId(Long marketItemId);
}
