package com.example.demo.tests;

import com.example.demo.client.OrderClient;
import com.example.demo.config.FeatureToggles;
import com.example.demo.dtos.FoodRating;
import com.example.demo.dtos.RatingResponse;
import com.example.demo.entities.FoodEntity;
import com.example.demo.entities.RatingEntity;
import com.example.demo.repositories.FoodRepository;
import com.example.demo.repositories.RatingRepository;
import com.example.demo.services.RatingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.http.ResponseEntity;

import java.util.Optional;
import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class RatingServiceTest {

    private RatingRepository ratingRepository;
    private FoodRepository foodRepository;
    private OrderClient orderClient;
    private FeatureToggles featureToggles;

    private RatingService ratingService;

    @BeforeEach
    void setUp() {
        ratingRepository = mock(RatingRepository.class);
        foodRepository = mock(FoodRepository.class);
        orderClient = mock(OrderClient.class);
        featureToggles = mock(FeatureToggles.class);

        ratingService = Mockito.spy(
                new RatingService(ratingRepository, foodRepository, orderClient, featureToggles)
        );
    }

    private void mockUser(UUID userId) {
        doReturn(userId).when(ratingService).getUserIdFromContext();
    }

    private void mockFood(UUID foodId) {
        FoodEntity food = new FoodEntity();
        food.setId(foodId);
        when(foodRepository.findById(foodId)).thenReturn(Optional.of(food));
    }

    /* ===================== BASIC FLOW ===================== */

    @Test
    void rateFood_success() throws Exception {
        UUID foodId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        mockUser(userId);
        doReturn(ResponseEntity.ok(true))
                .when(orderClient)
                .checkHasOrdered(foodId);
        when(ratingRepository.hasRateConcreteFood(foodId, userId))
                .thenReturn(false);
        when(ratingRepository.calculateAverageRatingForFood(foodId))
                .thenReturn(4.0);

        mockFood(foodId);

        RatingResponse response =
                ratingService.rateFood(foodId, new FoodRating(4.0));

        assertEquals(4.0, response.getAmount());
        verify(ratingRepository).save(any(RatingEntity.class));
    }

    /* ===================== BUG: allow rating without order ===================== */

    @Test
    void rateFood_withoutOrder_allowed_whenBugEnabled() throws Exception {
        UUID foodId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        mockUser(userId);
        when(featureToggles.isBugAllowRatingWithoutOrder()).thenReturn(true);
        when(ratingRepository.hasRateConcreteFood(foodId, userId)).thenReturn(false);
        when(ratingRepository.calculateAverageRatingForFood(foodId)).thenReturn(5.0);

        mockFood(foodId);

        assertDoesNotThrow(() ->
                ratingService.rateFood(foodId, new FoodRating(5.0))
        );
    }

    /* ===================== BUG: duplicate rating save ===================== */

    @Test
    void rateFood_duplicateSave_whenBugEnabled() throws Exception {
        UUID foodId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        mockUser(userId);
        doReturn(ResponseEntity.ok(true))
                .when(orderClient)
                .checkHasOrdered(foodId);
        when(ratingRepository.hasRateConcreteFood(foodId, userId))
                .thenReturn(false);
        when(featureToggles.isBugDuplicateRatingSave()).thenReturn(true);
        when(ratingRepository.calculateAverageRatingForFood(foodId))
                .thenReturn(3.0);

        mockFood(foodId);

        ratingService.rateFood(foodId, new FoodRating(3.0));

        verify(ratingRepository, times(2)).save(any());
    }

    /* ===================== BUG: partial save ===================== */

    @Test
    void rateFood_foodNotUpdated_whenPartialSaveBugEnabled() throws Exception {
        UUID foodId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        mockUser(userId);
        doReturn(ResponseEntity.ok(true))
                .when(orderClient)
                .checkHasOrdered(foodId);
        when(ratingRepository.hasRateConcreteFood(foodId, userId))
                .thenReturn(false);
        when(featureToggles.isBugPartialSave()).thenReturn(true);

        ratingService.rateFood(foodId, new FoodRating(4.0));

        verify(foodRepository, never()).save(any());
    }

    /* ===================== BUG: avg rating incorrect ===================== */

    @Test
    void editRating_avgRatingIncorrect_whenBugEnabled() throws Exception {
        UUID foodId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        RatingEntity entity = RatingEntity.builder()
                .ratingId(UUID.randomUUID())
                .foodId(foodId)
                .userId(userId)
                .rating(3.0)
                .build();

        mockUser(userId);
        doReturn(ResponseEntity.ok(true))
                .when(orderClient)
                .checkHasOrdered(foodId);
        when(ratingRepository.hasRateConcreteFood(foodId, userId))
                .thenReturn(true);
        when(ratingRepository.findByFoodIdAndUserId(foodId, userId))
                .thenReturn(Optional.of(entity));
        when(ratingRepository.calculateAverageRatingForFood(foodId))
                .thenReturn(4.0);
        when(featureToggles.isBugAvgRatingIncorrect()).thenReturn(true);

        mockFood(foodId);

        RatingResponse response =
                ratingService.editRating(foodId, new FoodRating(5.0));

        assertEquals(5.0, response.getAmount()); // 4.0 + 1.0
    }

    /* ===================== BUG: wrong user data ===================== */

    @Test
    void getUsersRating_returnsWrongUserData_whenBugEnabled() {
        UUID foodId = UUID.randomUUID();
        when(featureToggles.isBugWrongUserData()).thenReturn(true);

        double rating = ratingService.getUsersRating(foodId);

        assertEquals(5.0, rating);
    }

    /* ===================== BUG: food update not saved ===================== */

    @Test
    void foodRatingNotSavedToFood_whenBugEnabled() {
        UUID foodId = UUID.randomUUID();

        when(featureToggles.isBugFoodUpdateNotSaved()).thenReturn(true);

        ratingService.countRatingAmountForConcreteFood(foodId);

        verify(foodRepository, never()).save(any());
    }
}