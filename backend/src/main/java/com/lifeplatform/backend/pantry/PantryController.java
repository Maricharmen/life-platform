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
    public ResponseEntity createPantryItem(@RequestBody PantryItem item) {
        PantryItem savedItem = pantryService.saveOrUpdateItem(item);
        return new ResponseEntity<>(savedItem, HttpStatus.CREATED);
    }
}