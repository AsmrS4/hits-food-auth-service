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
public class FoodDetailsDto {
    UUID id;
    String name;
    String photo;
    Double price;
    Double rate;
    String description;
    List<Ingredient> ingredients;
    UUID categoryId;
}

