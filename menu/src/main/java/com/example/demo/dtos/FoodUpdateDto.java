package com.example.demo.dtos;

import com.example.demo.models.Ingredient;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Positive;
import jakarta.validation.constraints.Size;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.UUID;

@Data
public class FoodUpdateDto {
    private String name;

    @Size(max = 2000)
    private String description;

    @Positive
    private Double price;

    private UUID categoryId;

    private List<MultipartFile> newPhotos;

    private List<String> photosToDelete;

    private List<String> existingPhotos;

    private List<Ingredient> ingredients;

    private Boolean isAvailable;
}

