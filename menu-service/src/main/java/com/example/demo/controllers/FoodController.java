package com.example.demo.controllers;

import com.example.demo.dtos.*;
import com.example.demo.services.FoodService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/foods")
@RequiredArgsConstructor
public class FoodController {

    private final FoodService foodService;

    @PostMapping("/filter")
    public ResponseEntity<List<FoodShortDto>> getFiltered(@RequestBody(required = false) @Valid FoodFilterRequest filter) {
        return ResponseEntity.ok(foodService.getAllFoods(filter));
    }

    @GetMapping("/{id}")
    public ResponseEntity<FoodDetailsDto> getDetails(@PathVariable UUID id) {
        return ResponseEntity.ok(foodService.getFoodDetails(id));
    }
}



