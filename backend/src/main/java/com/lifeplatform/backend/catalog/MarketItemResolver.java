package com.lifeplatform.backend.catalog;

import org.springframework.stereotype.Service;

@Service
public class MarketItemResolver {

    private final MarketItemRepository marketItemRepository;
    private final AiMatchingAgent aiMatchingAgent;

    public MarketItemResolver(MarketItemRepository marketItemRepository, AiMatchingAgent aiMatchingAgent) {
        this.marketItemRepository = marketItemRepository;
        this.aiMatchingAgent = aiMatchingAgent;
    }

    public MarketItem resolve(String rawIngredientName) {
        String standardName = aiMatchingAgent.standardizeIngredient(rawIngredientName)
                .orElseThrow(() -> new IllegalArgumentException(
                        "No se pudo interpretar el ingrediente: " + rawIngredientName));

        return marketItemRepository.findByStandardNameIgnoreCase(standardName)
                .orElseGet(() -> {
                    MarketItem marketItem = new MarketItem();
                    marketItem.setStandardName(standardName);
                    return marketItemRepository.save(marketItem);
                });
    }
}
