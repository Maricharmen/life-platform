package com.lifeplatform.backend.pantry;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface PantryRepository extends JpaRepository<PantryItem, Long> {
    // JpaRepository ya incluye métodos como save(), findById(), delete() listos para usar.
}
