package com.example.demo.dtos;

import lombok.Data;

import java.util.UUID;

@Data
public class Category {
    UUID id;
    String name;
    String description;
}
