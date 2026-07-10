package com.lifeplatform.backend.pantry;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.Optional;

@Repository
public interface PantryRepository extends JpaRepository<PantryItem, Long> {
    Optional findByNameIgnoreCase(String name);
}
