package com.example.demo.controllers;

import com.example.demo.dtos.FoodRating;
import com.example.demo.dtos.RatingResponse;
import com.example.demo.services.RatingService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.apache.coyote.BadRequestException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RestController
@RequestMapping("/api/rate")
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Rating controller")
public class RateController {
    @Autowired
    private RatingService ratingService;
    @PostMapping("/{foodId}")
    @Operation(
            description = "Creating rate",
            summary = "This is summary for rate ordered food"
    )
    public ResponseEntity<RatingResponse> rateOrderedFood(@PathVariable UUID foodId, @RequestBody FoodRating rating) throws BadRequestException {
        return ResponseEntity.ok(ratingService.rateFood(foodId, rating));
    }
    @PutMapping("/{foodId}")
    @Operation(
            description = "Creating rate",
            summary = "This is summary for change rating for ordered food"
    )
    public ResponseEntity<RatingResponse> changeRateForOrderedFood(@PathVariable UUID foodId, @RequestBody FoodRating newRating) throws BadRequestException {
        return ResponseEntity.ok(ratingService.editRating(foodId, newRating));
    }
}
