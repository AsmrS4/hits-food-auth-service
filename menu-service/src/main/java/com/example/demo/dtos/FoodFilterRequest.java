package com.example.demo.dtos;

import com.example.demo.models.Ingredient;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FoodFilterRequest {
    List<Ingredient> includeIngredients;
    List<Ingredient> excludeIngredients;
    Double minPrice;
    Double maxPrice;
    String search;
    String sortBy;
    String sortDirection;
    UUID categoryId;
}
