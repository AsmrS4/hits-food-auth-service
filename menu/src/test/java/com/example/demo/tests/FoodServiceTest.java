package com.example.demo.tests;

import com.example.demo.config.FeatureToggles;
import com.example.demo.dtos.*;
import com.example.demo.entities.*;
import com.example.demo.mappers.FoodMapper;
import com.example.demo.repositories.CategoryRepository;
import com.example.demo.repositories.FoodRepository;
import com.example.demo.services.FileStorageService;
import com.example.demo.services.FoodService;
import com.example.demo.services.RatingService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.util.*;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class FoodServiceTest {

    private FoodRepository foodRepository;
    private CategoryRepository categoryRepository;
    private FoodMapper foodMapper;
    private RatingService ratingService;
    private FileStorageService fileStorageService;
    private FeatureToggles featureToggles;

    private FoodService foodService;

    @BeforeEach
    void setUp() {
        foodRepository = mock(FoodRepository.class);
        categoryRepository = mock(CategoryRepository.class);
        foodMapper = mock(FoodMapper.class);
        ratingService = mock(RatingService.class);
        fileStorageService = mock(FileStorageService.class);
        featureToggles = mock(FeatureToggles.class);

        foodService = new FoodService(
                foodRepository,
                categoryRepository,
                foodMapper,
                ratingService,
                fileStorageService,
                featureToggles
        );
    }

    private FoodEntity food(UUID id, double price, boolean deleted) {
        FoodEntity f = new FoodEntity();
        f.setId(id);
        f.setName("Food");
        f.setPrice(price);
        f.setIsDeleted(deleted);
        f.setPhotos(new ArrayList<>());
        f.setIngredientIds(new ArrayList<>());
        CategoryEntity c = new CategoryEntity();
        c.setId(UUID.randomUUID());
        f.setCategory(c);
        return f;
    }

    /* ---------- bugWrongPriceSorting ---------- */
    @Test
    void shouldSortPriceWrongWhenBugEnabled() {
        UUID f1 = UUID.randomUUID();
        UUID f2 = UUID.randomUUID();

        when(foodRepository.findByIsDeletedFalse()).thenReturn(
                List.of(food(f1, 10, false), food(f2, 5, false))
        );
        when(featureToggles.isBugWrongPriceSorting()).thenReturn(true);

        FoodFilterRequest filter = new FoodFilterRequest();
        filter.setSortBy("price");
        filter.setSortDirection("asc");

        foodService.getAllFoods(filter);

        verify(featureToggles).isBugWrongPriceSorting();
    }

    /* ---------- bugDeletedFoodVisible ---------- */
    @Test
    void deletedFoodAppearsWhenBugEnabled() {
        UUID id = UUID.randomUUID();

        when(foodRepository.findByIsDeletedFalse())
                .thenReturn(List.of(food(id, 5, false)));
        when(foodRepository.findAll())
                .thenReturn(List.of(food(id, 5, true)));
        when(featureToggles.isBugDeletedFoodVisible()).thenReturn(true);

        List<FoodShortDto> result = foodService.getAllFoods(null);

        assertTrue(result.size() > 1);
    }

    /* ---------- bugFoodDetailsWrongUserRating ---------- */
    @Test
    void wrongUserRatingReturnedWhenBugEnabled() {
        UUID foodId = UUID.randomUUID();
        FoodEntity entity = food(foodId, 5, false);

        when(foodRepository.findById(foodId)).thenReturn(Optional.of(entity));
        when(foodMapper.toDetailsDto(entity)).thenReturn(new FoodDetailsDto());
        when(featureToggles.isBugFoodDetailsWrongUserRating()).thenReturn(true);

        FoodDetailsResponse response = foodService.getFoodDetails(foodId);

        assertEquals(5.0, response.getUserRating());
    }

    /* ---------- bugFoodUpdateNotSaved ---------- */
    @Test
    void foodUpdateNotSavedToRepository() {
        UUID foodId = UUID.randomUUID();
        FoodEntity entity = food(foodId, 10, false);

        when(foodRepository.findById(foodId)).thenReturn(Optional.of(entity));

        FoodUpdateDto dto = new FoodUpdateDto();
        dto.setPrice(20.0);

        foodService.updateFood(foodId, dto);

        verify(foodRepository, never()).save(any());
    }
}