package com.lifeplatform.backend.recipes;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RecipeRepository extends JpaRepository<Recipe, Long> {
    // Listo con operaciones CRUD automáticas para las recetas
}
