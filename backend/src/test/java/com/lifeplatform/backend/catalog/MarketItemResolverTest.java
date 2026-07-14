package com.lifeplatform.backend.catalog;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.verifyNoInteractions;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class MarketItemResolverTest {

    @Mock
    private MarketItemRepository marketItemRepository;

    @Mock
    private AiMatchingAgent aiMatchingAgent;

    private MarketItemResolver marketItemResolver;

    @BeforeEach
    void setUp() {
        marketItemResolver = new MarketItemResolver(marketItemRepository, aiMatchingAgent);
    }

    @Test
    void resolvesExistingMarketItemWithoutCreatingNewOne() {
        MarketItem existing = new MarketItem(1L, "Arroz", "Granos");
        when(aiMatchingAgent.standardizeIngredient("arroz")).thenReturn(Optional.of("Arroz"));
        when(marketItemRepository.findByStandardNameIgnoreCase("Arroz")).thenReturn(Optional.of(existing));

        MarketItem result = marketItemResolver.resolve("arroz");

        assertThat(result).isSameAs(existing);
        verify(marketItemRepository, never()).save(any(MarketItem.class));
    }

    @Test
    void createsNewMarketItemWhenMissingInCatalog() {
        when(aiMatchingAgent.standardizeIngredient("couscous")).thenReturn(Optional.of("Couscous"));
        when(marketItemRepository.findByStandardNameIgnoreCase("Couscous")).thenReturn(Optional.empty());
        when(marketItemRepository.save(any(MarketItem.class))).thenAnswer(invocation -> {
            MarketItem item = invocation.getArgument(0);
            item.setIdMarketItem(99L);
            return item;
        });

        MarketItem result = marketItemResolver.resolve("couscous");

        assertThat(result.getIdMarketItem()).isEqualTo(99L);
        assertThat(result.getStandardName()).isEqualTo("Couscous");
        verify(marketItemRepository).save(any(MarketItem.class));
    }

    @Test
    void throwsIllegalArgumentExceptionWhenAiCannotStandardize() {
        when(aiMatchingAgent.standardizeIngredient("???")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> marketItemResolver.resolve("???"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("No se pudo interpretar el ingrediente");

        verifyNoInteractions(marketItemRepository);
    }

    @Test
    void throwsIllegalArgumentExceptionWhenInputNameIsNullOrBlank() {
        when(aiMatchingAgent.standardizeIngredient(null)).thenReturn(Optional.empty());
        when(aiMatchingAgent.standardizeIngredient("   ")).thenReturn(Optional.empty());

        assertThatThrownBy(() -> marketItemResolver.resolve(null))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("No se pudo interpretar el ingrediente");

        assertThatThrownBy(() -> marketItemResolver.resolve("   "))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("No se pudo interpretar el ingrediente");

        verify(marketItemRepository, never()).findByStandardNameIgnoreCase(any());
    }
}
