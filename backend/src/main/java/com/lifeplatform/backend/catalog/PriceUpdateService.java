package com.lifeplatform.backend.catalog;

import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PriceUpdateService {

    private final SupermarketItemRepository supermarketItemRepository;

    public PriceUpdateService(SupermarketItemRepository supermarketItemRepository) {
        this.supermarketItemRepository = supermarketItemRepository;
    }

    public List<SupermarketItem> updatePrices(List<SupermarketItem> prices) {
        return supermarketItemRepository.saveAll(prices);
    }
}
