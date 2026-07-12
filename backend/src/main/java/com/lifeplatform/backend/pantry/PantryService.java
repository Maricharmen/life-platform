package com.lifeplatform.backend.pantry;

import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class PantryService {

    private final PantryRepository pantryRepository;

    public PantryService(PantryRepository pantryRepository) {
        this.pantryRepository = pantryRepository;
    }

    public List<PantryItem> getAllItems() {
        return pantryRepository.findAll();
    }

    public Optional<PantryItem> getById(Long id) {
        return pantryRepository.findById(id);
    }

    public PantryItem saveOrUpdateItem(PantryItem newItem) {
        if (newItem.getQuantity() < 0) {
            throw new IllegalArgumentException("La cantidad no puede ser negativa");
        }

        Optional<PantryItem> existingItem = pantryRepository.findByNameIgnoreCase(newItem.getName());

        if (existingItem.isPresent()) {
            PantryItem itemToUpdate = existingItem.get();
            itemToUpdate.setQuantity(itemToUpdate.getQuantity() + newItem.getQuantity());
            itemToUpdate.setUnit(newItem.getUnit());
            itemToUpdate.setMarketItem(newItem.getMarketItem());
            return pantryRepository.save(itemToUpdate);
        }

        return pantryRepository.save(newItem);
    }

    public PantryItem updateItem(Long id, PantryItem updatedItem) {
        PantryItem existingItem = pantryRepository.findById(id)
                .orElseThrow(() -> new IllegalArgumentException("El item de despensa no existe"));
        existingItem.setName(updatedItem.getName());
        existingItem.setQuantity(updatedItem.getQuantity());
        existingItem.setUnit(updatedItem.getUnit());
        existingItem.setMarketItem(updatedItem.getMarketItem());
        return pantryRepository.save(existingItem);
    }

    public void deleteItem(Long id) {
        pantryRepository.deleteById(id);
    }
}