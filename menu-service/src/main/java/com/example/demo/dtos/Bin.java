package com.example.demo.dtos;

import lombok.Data;

import java.util.List;
import java.util.UUID;

@Data
public class Bin {
    UUID id;
    UUID clientId;
    List<Food> foodList;
}
