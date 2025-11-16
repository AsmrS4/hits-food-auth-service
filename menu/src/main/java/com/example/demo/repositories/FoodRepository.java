package com.example.demo.repositories;

import com.example.demo.entities.FoodEntity;
import com.example.demo.entities.CategoryEntity;
import com.example.demo.models.Ingredient;
import org.springframework.data.jpa.repository.*;
import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public interface FoodRepository extends JpaRepository<FoodEntity, UUID>, JpaSpecificationExecutor<FoodEntity> {
    List<FoodEntity> findByCategory(CategoryEntity category);
    List<FoodEntity> findByNameContainingIgnoreCase(String name);
    boolean existsByCategory(CategoryEntity category);
}

