package com.lifeplatform.backend.catalog;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface MarketItemRepository extends JpaRepository<MarketItem, Long> {
    Optional<MarketItem> findByStandardNameIgnoreCase(String standardName);
}
