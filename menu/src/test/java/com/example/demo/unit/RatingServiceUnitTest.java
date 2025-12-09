package com.example.demo.unit;

import com.example.demo.config.FeatureToggles;
import com.example.demo.repositories.FoodRepository;
import com.example.demo.repositories.RatingRepository;
import com.example.demo.services.RatingService;
import com.example.demo.client.OrderClient;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.util.UUID;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class RatingServiceUnitTest {

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

    @Test
    void shouldReturnTrueWhenUserHasRatedFood() {
        UUID foodId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        doReturn(userId).when(ratingService).getUserIdFromContext();
        when(ratingRepository.hasRateConcreteFood(foodId, userId)).thenReturn(true);

        boolean result = ratingService.hasRateFromConcreteUser(foodId);

        assertTrue(result);
        verify(ratingRepository).hasRateConcreteFood(foodId, userId);
    }

    @Test
    void shouldReturnFalseWhenUserHasNotRatedFood() {
        UUID foodId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        doReturn(userId).when(ratingService).getUserIdFromContext();
        when(ratingRepository.hasRateConcreteFood(foodId, userId)).thenReturn(false);

        boolean result = ratingService.hasRateFromConcreteUser(foodId);

        assertFalse(result);
    }

    @Test
    void shouldReturnFalseWhenUserIdIsNull() {
        UUID foodId = UUID.randomUUID();

        doReturn(null).when(ratingService).getUserIdFromContext();

        boolean result = ratingService.hasRateFromConcreteUser(foodId);

        assertFalse(result);
        verify(ratingRepository, never()).hasRateConcreteFood(any(), any());
    }

    @Test
    void shouldReturnFalseOnRepositoryException() {
        UUID foodId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        doReturn(userId).when(ratingService).getUserIdFromContext();
        when(ratingRepository.hasRateConcreteFood(any(), any()))
                .thenThrow(new RuntimeException("DB error"));

        boolean result = ratingService.hasRateFromConcreteUser(foodId);

        assertFalse(result);
    }

    @Test
    void shouldCallRepositoryWithCorrectArguments() {
        UUID foodId = UUID.randomUUID();
        UUID userId = UUID.randomUUID();

        doReturn(userId).when(ratingService).getUserIdFromContext();
        when(ratingRepository.hasRateConcreteFood(foodId, userId)).thenReturn(true);

        ratingService.hasRateFromConcreteUser(foodId);

        verify(ratingRepository).hasRateConcreteFood(foodId, userId);
    }
}
