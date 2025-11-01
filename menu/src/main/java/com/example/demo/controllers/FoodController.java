package com.example.demo.controllers;

import com.example.demo.dtos.*;
import com.example.demo.services.FoodService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/foods")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Menu controller")
public class FoodController {

    private final FoodService foodService;

    @PostMapping("/filter")
    @Operation(
            description = "Getting menu",
            summary = "This is summary for getting menu"
    )
    public ResponseEntity<List<FoodShortDto>> getFiltered(@RequestBody(required = false) @Valid FoodFilterRequest filter) {
        return ResponseEntity.ok(foodService.getAllFoods(filter));
    }

    @GetMapping("/{id}")
    @Operation(
            description = "Get dish details",
            summary = "This is summary for getting details about dish from menu"
    )
    public ResponseEntity<FoodDetailsResponse> getDetails(@PathVariable UUID id) {
        return ResponseEntity.ok(foodService.getFoodDetails(id));
    }

    @PostMapping
    @Operation(
            description = "Create a new dish",
            summary = "This is summary for creating a new dish"
    )
    public ResponseEntity<FoodDetailsDto> createFood(@RequestBody @Valid FoodCreateDto dto) {
        return ResponseEntity.ok(foodService.createFood(dto));
    }

    @PutMapping(value = "/{id}")
    public ResponseEntity<FoodDetailsDto> updateFood(
            @PathVariable UUID id,
            @ModelAttribute FoodUpdateDto dto) {

        FoodDetailsDto updatedFood = foodService.updateFood(id, dto);
        return ResponseEntity.ok(updatedFood);
    }

    @DeleteMapping("/{id}")
    @Operation(
            description = "Delete a dish",
            summary = "This is summary for deleting an existing dish"
    )
    public ResponseEntity<Void> deleteFood(@PathVariable UUID id) {
        foodService.deleteFood(id);
        return ResponseEntity.noContent().build();
    }

    @PatchMapping("/{id}/availability")
    @Operation(
            description = "Edit dish's availability",
            summary = "This is summary for edit availability of an existing dish"
    )
    public ResponseEntity<FoodDetailsDto> setAvailability(@PathVariable UUID id, @RequestParam boolean available) {
        return ResponseEntity.ok(foodService.setAvailability(id, available));
    }
}



