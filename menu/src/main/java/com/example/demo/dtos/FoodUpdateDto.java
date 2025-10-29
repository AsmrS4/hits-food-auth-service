package com.example.demo.dtos;

import com.example.demo.models.Ingredient;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Data
public class FoodUpdateDto {
    private String name;
    private UUID categoryId;
    private MultipartFile photo;
    private Double rate;
    private Double price;
    private String description;
    private List<Ingredient> ingredients;
    private Boolean isAvailable;
}

