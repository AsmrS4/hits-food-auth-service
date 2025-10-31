package com.example.demo.dtos;

import com.example.demo.models.Ingredient;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class FoodUpdateDto {
    private String name;
    private UUID categoryId;
    private List<String> photos;
    private Double rate;
    private Double price;
    private String description;
    private List<Ingredient> ingredients;
    private Boolean isAvailable;
}

