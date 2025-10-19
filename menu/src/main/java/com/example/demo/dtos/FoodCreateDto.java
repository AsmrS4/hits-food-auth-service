package com.example.demo.dtos;

import com.example.demo.models.Ingredient;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class FoodCreateDto {
    private String name;
    private UUID categoryId;
    private String photo;
    private Double rate;
    private Double price;
    private String description;
    private List<Ingredient> ingredients;
}

