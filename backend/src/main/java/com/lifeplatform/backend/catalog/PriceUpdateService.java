package com.lifeplatform.backend.catalog;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@Service
public class PriceUpdateService {

    private final SupermarketItemRepository supermarketItemRepository;
    private final MarketItemResolver marketItemResolver;

    public PriceUpdateService(SupermarketItemRepository supermarketItemRepository,
                              MarketItemResolver marketItemResolver) {
        this.supermarketItemRepository = supermarketItemRepository;
        this.marketItemResolver = marketItemResolver;
    }

    @Transactional
    public List<SupermarketItem> updatePrices(List<BulkUpdateSupermarketPriceRequestDTO> prices) {
        if (prices == null || prices.isEmpty()) {
            return List.of();
        }

        List<SupermarketItem> itemsToSave = new ArrayList<>();
        for (BulkUpdateSupermarketPriceRequestDTO request : prices) {
            MarketItem marketItem = marketItemResolver.resolve(request.getIngredientName());

            Optional<SupermarketItem> existingItem = supermarketItemRepository
                    .findByMarketItem_IdMarketItemAndSupermarketName(marketItem.getIdMarketItem(), request.getSupermarketName());

            SupermarketItem item = existingItem.orElseGet(SupermarketItem::new);
            item.setMarketItem(marketItem);
            item.setSupermarketName(request.getSupermarketName());
            item.setPackagePrice(request.getPackagePrice());
            item.setPackageSize(request.getPackageSize());
            item.setUnit(request.getUnit());
            itemsToSave.add(item);
        }

        return supermarketItemRepository.saveAll(itemsToSave);
    }

    @Scheduled(cron = "0 0 2 * * *")
    public void nightlyRefreshUnitPrices() {
        List<SupermarketItem> allItems = supermarketItemRepository.findAll();
        if (allItems.isEmpty()) {
            return;
        }

        // TODO: In production, pull fresh price data from supermarket connectors before saving.
        supermarketItemRepository.saveAll(allItems);
    }
}
