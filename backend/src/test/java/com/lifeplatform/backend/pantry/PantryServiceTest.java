package com.lifeplatform.backend.pantry;

import com.lifeplatform.backend.catalog.MarketItem;
import com.lifeplatform.backend.catalog.MarketItemResolver;
import com.lifeplatform.backend.shared.ResourceNotFoundException;
import com.lifeplatform.backend.shared.UnitConverter;
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
class PantryServiceTest {

    @Mock
    private PantryRepository pantryRepository;

    @Mock
    private MarketItemResolver marketItemResolver;

    private PantryService pantryService;

    @BeforeEach
    void setUp() {
        pantryService = new PantryService(pantryRepository, marketItemResolver, new UnitConverter());
    }

    @Test
    void createsNewRowWhenIngredientDoesNotExistInPantry() {
        AddPantryItemRequestDTO request = new AddPantryItemRequestDTO("Arroz", 2.0, "kg");
        MarketItem marketItem = new MarketItem();
        marketItem.setIdMarketItem(1L);
        marketItem.setStandardName("Arroz");

        when(marketItemResolver.resolve("Arroz")).thenReturn(marketItem);
        when(pantryRepository.findByIngredientNameIgnoreCase("Arroz")).thenReturn(Optional.empty());
        when(pantryRepository.save(any(PantryItem.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PantryItem result = pantryService.addToPantry(request);

        assertThat(result.getIngredientName()).isEqualTo("Arroz");
        assertThat(result.getQuantity()).isEqualTo(2.0);
        assertThat(result.getUnit()).isEqualTo("kg");
        assertThat(result.getMarketItem()).isSameAs(marketItem);
        verify(marketItemResolver).resolve("Arroz");
        verify(pantryRepository).save(any(PantryItem.class));
    }

    @Test
    void addsQuantityWhenIngredientRowAlreadyExistsAndDoesNotCreateAnotherRow() {
        AddPantryItemRequestDTO request = new AddPantryItemRequestDTO("Arroz", 300.0, "g");
        MarketItem marketItem = new MarketItem();
        marketItem.setIdMarketItem(1L);
        marketItem.setStandardName("Arroz");

        PantryItem existing = new PantryItem();
        existing.setIdPantryItem(10L);
        existing.setIngredientName("Arroz");
        existing.setQuantity(700.0);
        existing.setUnit("g");
        existing.setMarketItem(marketItem);

        when(marketItemResolver.resolve("Arroz")).thenReturn(marketItem);
        when(pantryRepository.findByIngredientNameIgnoreCase("Arroz")).thenReturn(Optional.of(existing));
        when(pantryRepository.save(any(PantryItem.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PantryItem result = pantryService.addToPantry(request);

        assertThat(result.getIdPantryItem()).isEqualTo(10L);
        assertThat(result.getQuantity()).isEqualTo(1000.0);
        assertThat(result.getUnit()).isEqualTo("g");
        verify(pantryRepository).save(existing);
    }

    @Test
    void addsCorrectlyWhenUnitsAreDifferent() {
        AddPantryItemRequestDTO request = new AddPantryItemRequestDTO("Arroz", 1.0, "kg");
        MarketItem marketItem = new MarketItem();
        marketItem.setIdMarketItem(1L);
        marketItem.setStandardName("Arroz");

        PantryItem existing = new PantryItem();
        existing.setIdPantryItem(11L);
        existing.setIngredientName("Arroz");
        existing.setQuantity(500.0);
        existing.setUnit("g");
        existing.setMarketItem(marketItem);

        when(marketItemResolver.resolve("Arroz")).thenReturn(marketItem);
        when(pantryRepository.findByIngredientNameIgnoreCase("Arroz")).thenReturn(Optional.of(existing));
        when(pantryRepository.save(any(PantryItem.class))).thenAnswer(invocation -> invocation.getArgument(0));

        PantryItem result = pantryService.addToPantry(request);

        assertThat(result.getQuantity()).isEqualTo(1500.0);
        assertThat(result.getUnit()).isEqualTo("g");
    }

    @Test
    void rejectsNullQuantityBeforeAnyRepositoryOperation() {
        AddPantryItemRequestDTO request = new AddPantryItemRequestDTO("Arroz", null, "kg");

        assertThatThrownBy(() -> pantryService.addToPantry(request))
                .isInstanceOf(IllegalArgumentException.class);

        verifyNoInteractions(marketItemResolver, pantryRepository);
    }

    @Test
    void rejectsNegativeQuantityBeforeAnyRepositoryOperation() {
        AddPantryItemRequestDTO request = new AddPantryItemRequestDTO("Arroz", -1.0, "kg");

        assertThatThrownBy(() -> pantryService.addToPantry(request))
                .isInstanceOf(IllegalArgumentException.class);

        verifyNoInteractions(marketItemResolver, pantryRepository);
    }

    @Test
    void rejectsNullOrBlankNameBeforeAnyRepositoryOperation() {
        AddPantryItemRequestDTO nullNameRequest = new AddPantryItemRequestDTO(null, 1.0, "kg");
        AddPantryItemRequestDTO blankNameRequest = new AddPantryItemRequestDTO("   ", 1.0, "kg");

        assertThatThrownBy(() -> pantryService.addToPantry(nullNameRequest))
                .isInstanceOf(IllegalArgumentException.class);

        assertThatThrownBy(() -> pantryService.addToPantry(blankNameRequest))
                .isInstanceOf(IllegalArgumentException.class);

        verifyNoInteractions(marketItemResolver, pantryRepository);
    }

    @Test
    void updateQuantityUpdatesQuantityAndUnitWhenIdExists() {
        UpdatePantryItemRequestDTO request = new UpdatePantryItemRequestDTO(2.5, "kg");

        PantryItem existing = new PantryItem();
        existing.setIdPantryItem(20L);
        existing.setQuantity(1.0);
        existing.setUnit("kg");

        when(pantryRepository.findById(20L)).thenReturn(Optional.of(existing));
        when(pantryRepository.save(existing)).thenReturn(existing);

        PantryItem result = pantryService.updateQuantity(20L, request);

        assertThat(result.getQuantity()).isEqualTo(2.5);
        assertThat(result.getUnit()).isEqualTo("kg");
        verify(pantryRepository).save(existing);
    }

    @Test
    void updateQuantityThrowsResourceNotFoundWhenIdDoesNotExist() {
        UpdatePantryItemRequestDTO request = new UpdatePantryItemRequestDTO(2.5, "kg");
        when(pantryRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> pantryService.updateQuantity(999L, request))
                .isInstanceOf(ResourceNotFoundException.class)
                .hasMessage("El item de despensa no existe");

        verify(pantryRepository, never()).save(any(PantryItem.class));
    }

    @Test
    void deleteItemDeletesById() {
        pantryService.deleteItem(77L);

        verify(pantryRepository).deleteById(77L);
    }
}
