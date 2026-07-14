package com.lifeplatform.backend.pantry;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/pantry")
public class PantryController {

    private final PantryService pantryService;

    public PantryController(PantryService pantryService) {
        this.pantryService = pantryService;
    }

    @GetMapping
    public ResponseEntity<List<PantryItem>> getAllPantryItems() {
        return new ResponseEntity<>(pantryService.getAllItems(), HttpStatus.OK);
    }

    @PostMapping
    public ResponseEntity<PantryItem> createPantryItem(@Valid @RequestBody AddPantryItemRequestDTO request) {
        return new ResponseEntity<>(pantryService.addToPantry(request), HttpStatus.CREATED);
    }

    @GetMapping("/{id}")
    public ResponseEntity<PantryItem> getPantryItem(@PathVariable Long id) {
        return pantryService.getById(id)
                .map(item -> new ResponseEntity<>(item, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PutMapping("/{id}")
    public ResponseEntity<PantryItem> updatePantryItem(@PathVariable Long id,
                                                        @Valid @RequestBody UpdatePantryItemRequestDTO request) {
        return new ResponseEntity<>(pantryService.updateQuantity(id, request), HttpStatus.OK);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deletePantryItem(@PathVariable Long id) {
        pantryService.deleteItem(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }
}