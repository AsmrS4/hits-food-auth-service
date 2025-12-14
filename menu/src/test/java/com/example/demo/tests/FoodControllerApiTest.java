package com.example.demo.tests;

import com.example.demo.client.OrderClient;
import com.example.demo.config.FeatureToggles;
import com.example.demo.dtos.FoodRating;
import com.example.demo.dtos.RatingResponse;
import com.example.demo.entities.CategoryEntity;
import com.example.demo.entities.FoodEntity;
import com.example.demo.entities.RatingEntity;
import com.example.demo.repositories.FoodRepository;
import com.example.demo.repositories.RatingRepository;
import com.example.demo.services.RatingService;
import org.apache.coyote.BadRequestException;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

public class FoodControllerApiTest {

    private RatingRepository ratingRepository;
    private FoodRepository foodRepository;
    private OrderClient client;
    private FeatureToggles toggles;

    private RatingService service;

    @BeforeEach
    void setup() {
        ratingRepository = mock(RatingRepository.class);
        foodRepository = mock(FoodRepository.class);
        client = mock(OrderClient.class);
        toggles = new FeatureToggles();

        service = Mockito.spy(new RatingService(
                ratingRepository,
                foodRepository,
                client,
                toggles
        ));
    }

    private FoodEntity mockFood(UUID id) {
        FoodEntity food = new FoodEntity();
        food.setId(id);
        food.setName("Burger");
        food.setDescription("Test desc");
        food.setPrice(5.0);
        food.setRate(0.0);
        food.setPhotos(new ArrayList<>());
        food.setIsAvailable(true);
        food.setIsDeleted(false);
        food.setIngredientIds(new ArrayList<>());
        food.setCategory(new CategoryEntity());
        return food;
    }

    @Test
    void rateFood_success() throws Exception {
        UUID foodId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        doReturn(userId).when(service).getUserIdFromContext();
        Mockito.<ResponseEntity<?>>when(client.checkHasOrdered(eq(foodId))).thenReturn(ResponseEntity.ok(true));
        when(ratingRepository.hasRateConcreteFood(foodId, userId)).thenReturn(false);
        when(ratingRepository.calculateAverageRatingForFood(foodId)).thenReturn(4.0);

        FoodEntity food = mockFood(foodId);
        when(foodRepository.findById(foodId)).thenReturn(Optional.of(food));

        RatingResponse response = service.rateFood(foodId, new FoodRating(4.0));

        assertEquals(4.0, response.getAmount());
        verify(ratingRepository, times(1)).save(any());
    }

    @Test
    void rateFood_alreadyRated() throws Exception {
        UUID foodId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        doReturn(userId).when(service).getUserIdFromContext();
        Mockito.<ResponseEntity<?>>when(client.checkHasOrdered(eq(foodId))).thenReturn(ResponseEntity.ok(true));
        when(ratingRepository.hasRateConcreteFood(foodId, userId)).thenReturn(true);

        assertThrows(BadRequestException.class, () ->
                service.rateFood(foodId, new FoodRating(5.0))
        );
    }

    @Test
    void editRating_success() throws Exception {
        UUID foodId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        RatingEntity existing = RatingEntity.builder()
                .ratingId(UUID.randomUUID())
                .foodId(foodId)
                .userId(userId)
                .rating(3.0)
                .build();

        doReturn(userId).when(service).getUserIdFromContext();
        Mockito.<ResponseEntity<?>>when(client.checkHasOrdered(eq(foodId))).thenReturn(ResponseEntity.ok(true));
        when(ratingRepository.hasRateConcreteFood(foodId, userId)).thenReturn(true);
        when(ratingRepository.findByFoodIdAndUserId(foodId, userId))
                .thenReturn(Optional.of(existing));
        when(ratingRepository.calculateAverageRatingForFood(foodId)).thenReturn(4.5);

        FoodEntity food = mockFood(foodId);
        when(foodRepository.findById(foodId)).thenReturn(Optional.of(food));

        RatingResponse response = service.editRating(foodId, new FoodRating(5.0));

        assertEquals(4.5, response.getAmount());
        verify(ratingRepository, times(1)).save(existing);
    }

    @Test
    void getUsersRating_noRating() {
        UUID foodId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        doReturn(userId).when(service).getUserIdFromContext();
        when(ratingRepository.findByFoodIdAndUserId(foodId, userId)).thenReturn(Optional.empty());

        double result = service.getUsersRating(foodId);

        assertEquals(-1, result);
    }

    @Test
    void deleteRating_success() {
        UUID foodId = UUID.randomUUID();
        service.deleteRatingByFood(foodId);
        verify(ratingRepository, times(1)).deleteRatingsByFoodId(foodId);
    }
}
