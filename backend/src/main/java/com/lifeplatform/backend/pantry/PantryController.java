package com.lifeplatform.backend.pantry;

import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@CrossOrigin(origins = "*")
@RestController
@RequestMapping("/api/pantry")
public class PantryController {

    @Autowired
    private PantryService pantryService; 

    @GetMapping
    public ResponseEntity getAllPantryItems() {
        List items = pantryService.getAllItems();
        return new ResponseEntity<>(items, HttpStatus.OK);
    }


    @PostMapping
    public ResponseEntity<PantryItem> createPantryItem(@RequestBody PantryItem item) {
        PantryItem savedItem = pantryService.saveOrUpdateItem(item);
        return new ResponseEntity<>(savedItem, HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PantryItem> getPantryItem(@PathVariable Long id) {
        return pantryService.getById(id)
                .map(item -> new ResponseEntity<>(item, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PantryItem> updatePantryItem(@PathVariable Long id, @RequestBody PantryItem item) {
        PantryItem updated = pantryService.updateItem(id, item);
        return new ResponseEntity<>(updated, HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePantryItem(@PathVariable Long id) {
        pantryService.deleteItem(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}