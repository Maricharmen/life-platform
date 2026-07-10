package com.lifeplatform.backend.pantry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.Optional;

@Service
public class PantryService {

    @Autowired
    private PantryRepository pantryRepository;

    public List getAllItems() {
        return pantryRepository.findAll();
    }

    public PantryItem saveOrUpdateItem(PantryItem newItem) {
        
        if (newItem.getQuantity() < 0) {
            throw new IllegalArgumentException("La cantidad no puede ser negativa");
        }

        Optional<PantryItem> existingItem = pantryRepository.findByNameIgnoreCase(newItem.getName());
        
        if (existingItem.isPresent()) {
            PantryItem itemToUpdate = existingItem.get();
            itemToUpdate.setQuantity(itemToUpdate.getQuantity() + newItem.getQuantity());
            return pantryRepository.save(itemToUpdate);
        } else {
            return pantryRepository.save(newItem);
        }
    }
}