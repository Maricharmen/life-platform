package com.lifeplatform.backend.pantry;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/pantry")
public class PantryController {

    @Autowired
    private PantryRepository pantryRepository;

    @PostMapping
    public ResponseEntity<PantryItem> createPantryItem(@RequestBody PantryItem item) {
        PantryItem savedItem = pantryRepository.save(item);
        return new ResponseEntity<>(savedItem, HttpStatus.CREATED);
    }
}