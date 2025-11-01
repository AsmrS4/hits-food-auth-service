package com.example.demo.dtos;

import com.example.demo.models.Ingredient;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class FoodUpdateDto {
    @NotBlank(message = "Name field is required")
    private String name;
    @NotBlank(message = "Category field is required")
    private UUID categoryId;
    @NotBlank(message = "Photo is required")
    @NotEmpty(message = "Photo is required")
    private List<String> photos;
    @NotBlank(message = "Rating is required")
    private Double rate;
    @NotBlank(message = "Price is required")
    private Double price;
    @NotBlank(message = "Description is required")
    private String description;
    @NotBlank(message = "Ingredients are required")
    @NotEmpty(message = "Ingredients are required")
    private List<Ingredient> ingredients;
    private Boolean isAvailable;
}

