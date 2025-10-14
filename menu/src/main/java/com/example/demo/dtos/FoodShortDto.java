package com.example.demo.dtos;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class FoodShortDto {
    UUID id;
    String name;
    String photo;
    Double price;
    Double rate;
    String description;
    UUID categoryId;
}

