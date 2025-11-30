package com.example.demo.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FoodShortDto {
    UUID id;
    String name;
    List<String> photos;
    Double price;
    Double rate;
    String description;
    UUID categoryId;
    Boolean isAvailable;
    Boolean isDeleted;
}

