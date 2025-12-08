package com.example.demo.dtos;

import lombok.Data;

import java.util.UUID;

@Data
public class Rating {
    UUID id;
    UUID clientId;
    UUID foodId;
    Double rating;
}
