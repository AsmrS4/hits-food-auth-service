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

    @PostMapping
    public ResponseEntity<FoodDetailsDto> createFood(@RequestBody @Valid FoodCreateDto dto) {
        return ResponseEntity.ok(foodService.createFood(dto));
    }

    @PutMapping("/{id}")
    public ResponseEntity<FoodDetailsDto> updateFood(@PathVariable UUID id, @RequestBody @Valid FoodUpdateDto dto) {
        return ResponseEntity.ok(foodService.updateFood(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteFood(@PathVariable UUID id) {
        foodService.deleteFood(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/availability")
    public ResponseEntity<FoodDetailsDto> setAvailability(@PathVariable UUID id, @RequestParam boolean available) {
        return ResponseEntity.ok(foodService.setAvailability(id, available));
    }
}



