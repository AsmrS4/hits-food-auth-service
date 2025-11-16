package com.example.demo.dtos;

import com.example.demo.models.Ingredient;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class Food {
    UUID id;
    String name;
    String description;
    Double price;
    Double rate;
    String photo;
    Boolean isAvailable;
    List<Ingredient> ingredientIds;
    Category category;
}
