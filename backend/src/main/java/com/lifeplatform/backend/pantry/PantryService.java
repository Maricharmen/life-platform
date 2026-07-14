package com.lifeplatform.backend.pantry;

import com.lifeplatform.backend.catalog.MarketItem;
import com.lifeplatform.backend.catalog.MarketItemResolver;
import com.lifeplatform.backend.shared.ResourceNotFoundException;
import com.lifeplatform.backend.shared.UnitConverter;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
public class PantryService {

    private final PantryRepository pantryRepository;
    private final MarketItemResolver marketItemResolver;
    private final UnitConverter unitConverter;

    public PantryService(PantryRepository pantryRepository,
                         MarketItemResolver marketItemResolver,
                          UnitConverter unitConverter) {
        this.pantryRepository = pantryRepository;
        this.marketItemResolver = marketItemResolver;
        this.unitConverter = unitConverter;
    }

    public List<PantryItem> getAllItems() {
        return pantryRepository.findAll();
    }

    public Optional<PantryItem> getById(Long id) {
        return pantryRepository.findById(id);
    }

    @Transactional
    public PantryItem addToPantry(AddPantryItemRequestDTO request) {
        validateAddRequest(request);
        MarketItem marketItem = marketItemResolver.resolve(request.getIngredientName());

        return pantryRepository.findByIngredientNameIgnoreCase(request.getIngredientName())
                .map(existing -> mergeQuantity(existing, request.getQuantity(), request.getUnit()))
                .orElseGet(() -> createNewPantryRow(marketItem, request.getIngredientName(), request.getQuantity(), request.getUnit()));
    }

    public PantryItem updateQuantity(Long id, UpdatePantryItemRequestDTO request) {
        PantryItem existing = pantryRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("El item de despensa no existe"));

        existing.setQuantity(request.getQuantity());
        existing.setUnit(request.getUnit());
        
        return pantryRepository.save(existing);
    }

    public void deleteItem(Long id) {
        pantryRepository.deleteById(id);
    }

    private PantryItem mergeQuantity(PantryItem existing, Double addedQuantity, String addedUnit) {
        double currentBase = unitConverter.toBaseUnit(existing.getQuantity(), existing.getUnit());
        double addedBase = unitConverter.toBaseUnit(addedQuantity, addedUnit);
        existing.setQuantity(unitConverter.fromBaseUnit(currentBase + addedBase, existing.getUnit()));
        return pantryRepository.save(existing);
    }

    private PantryItem createNewPantryRow(MarketItem marketItem, String ingredientName, Double quantity, String unit) {
        PantryItem item = new PantryItem();
        item.setIngredientName(ingredientName);
        item.setQuantity(quantity);
        item.setUnit(unit);
        item.setMarketItem(marketItem);
        return pantryRepository.save(item);
    }

    private void validateAddRequest(AddPantryItemRequestDTO request) {
        if (request == null) {
            throw new IllegalArgumentException("La solicitud no puede ser nula");
        }
        if (request.getIngredientName() == null || request.getIngredientName().trim().isEmpty()) {
            throw new IllegalArgumentException("Debes indicar el nombre del ingrediente");
        }
        if (request.getQuantity() == null || request.getQuantity() <= 0) {
            throw new IllegalArgumentException("La cantidad debe ser mayor a cero");
        }
        if (request.getUnit() == null || request.getUnit().trim().isEmpty()) {
            throw new IllegalArgumentException("La unidad es obligatoria");
        }
    }

}