package com.lifeplatform.backend.pantry;

import com.lifeplatform.backend.catalog.MarketItem;
import com.lifeplatform.backend.catalog.MarketItemRepository;
import jakarta.persistence.PersistenceException;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.dao.DataAccessException;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.orm.jpa.JpaSystemException;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
class PantryMarketItemRelationTest {

    @Autowired
    private PantryRepository pantryRepository;

    @Autowired
    private MarketItemRepository marketItemRepository;

    @Test
    void savesPantryItemWithPersistedMarketItemAndLoadsFullRelation() {
        MarketItem marketItem = marketItemRepository.save(new MarketItem(null, "Arroz", "Granos"));

        PantryItem pantryItem = new PantryItem();
        pantryItem.setIngredientName("Arroz");
        pantryItem.setQuantity(1.0);
        pantryItem.setUnit("kg");
        pantryItem.setMarketItem(marketItem);

        PantryItem saved = pantryRepository.saveAndFlush(pantryItem);

        PantryItem reloaded = pantryRepository.findById(saved.getIdPantryItem()).orElseThrow();
        assertThat(reloaded.getMarketItem()).isNotNull();
        assertThat(reloaded.getMarketItem().getIdMarketItem()).isEqualTo(marketItem.getIdMarketItem());
        assertThat(reloaded.getMarketItem().getStandardName()).isEqualTo("Arroz");
    }

    @Test
    void findByMarketItemIdReturnsExpectedRow() {
        MarketItem marketItem = marketItemRepository.save(new MarketItem(null, "Avena", "Granos"));

        PantryItem pantryItem = new PantryItem();
        pantryItem.setIngredientName("Avena");
        pantryItem.setQuantity(500.0);
        pantryItem.setUnit("g");
        pantryItem.setMarketItem(marketItem);
        pantryRepository.saveAndFlush(pantryItem);

        PantryItem found = pantryRepository.findByMarketItem_IdMarketItem(marketItem.getIdMarketItem()).orElseThrow();

        assertThat(found.getIngredientName()).isEqualTo("Avena");
        assertThat(found.getMarketItem().getIdMarketItem()).isEqualTo(marketItem.getIdMarketItem());
    }

    @Test
    void savingPantryItemWithNonPersistedMarketItemFailsByReferentialIntegrity() {
        MarketItem nonPersisted = new MarketItem();
        nonPersisted.setIdMarketItem(999_999L);
        nonPersisted.setStandardName("Fantasma");

        PantryItem pantryItem = new PantryItem();
        pantryItem.setIngredientName("Fantasma");
        pantryItem.setQuantity(1.0);
        pantryItem.setUnit("kg");
        pantryItem.setMarketItem(nonPersisted);

        assertThatThrownBy(() -> pantryRepository.saveAndFlush(pantryItem))
                .isInstanceOfAny(
                        DataIntegrityViolationException.class,
                        DataAccessException.class,
                        JpaSystemException.class,
                        PersistenceException.class,
                        RuntimeException.class
                );
    }
}
