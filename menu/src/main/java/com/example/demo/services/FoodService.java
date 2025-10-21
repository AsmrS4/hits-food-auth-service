package com.example.demo.services;

import com.example.demo.dtos.*;
import com.example.demo.entities.*;
import com.example.demo.mappers.FoodMapper;
import com.example.demo.repositories.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.*;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class FoodService {
    private final FoodRepository foodRepository;
    private final CategoryRepository categoryRepository;
    private final FoodMapper foodMapper;
    private final RatingService ratingService;

    public List<FoodShortDto> getAllFoods(FoodFilterRequest filter) {
        List<FoodEntity> foods = foodRepository.findAll();
        if (filter == null) {
            List<FoodShortDto> foodShortDtos = foodMapper.toShortDtoList(foods);
            return foodShortDtos.stream().map(dto -> {
                double amountRating = ratingService.countRatingAmountForConcreteFood(dto.getId());
                dto.setRate(amountRating);
                return dto;
            }).collect(Collectors.toList());
        }
        if (filter.getCategoryId() != null) {
            foods = foods.stream()
                    .filter(f -> f.getCategory().getId().equals(filter.getCategoryId()))
                    .toList();
        }
        if (filter.getIncludeIngredients() != null && !filter.getIncludeIngredients().isEmpty()) {
            foods = foods.stream()
                    .filter(f -> new HashSet<>(f.getIngredientIds()).containsAll(filter.getIncludeIngredients()))
                    .toList();
        }
        if (filter.getExcludeIngredients() != null && !filter.getExcludeIngredients().isEmpty()) {
            foods = foods.stream()
                    .filter(f -> f.getIngredientIds().stream()
                            .noneMatch(filter.getExcludeIngredients()::contains))
                    .toList();
        }
        if (filter.getMinPrice() != null) {
            foods = foods.stream()
                    .filter(f -> f.getPrice() >= filter.getMinPrice())
                    .toList();
        }
        if (filter.getMaxPrice() != null) {
            foods = foods.stream()
                    .filter(f -> f.getPrice() <= filter.getMaxPrice())
                    .toList();
        }
        if (filter.getSearch() != null && !filter.getSearch().isBlank()) {
            String searchLower = filter.getSearch().toLowerCase();
            foods = foods.stream()
                    .filter(f -> f.getName().toLowerCase().contains(searchLower))
                    .toList();
        }
        if ("price".equalsIgnoreCase(filter.getSortBy())) {
            foods = foods.stream()
                    .sorted(Comparator.comparing(FoodEntity::getPrice,
                            "desc".equalsIgnoreCase(filter.getSortDirection())
                                    ? Comparator.reverseOrder() : Comparator.naturalOrder()))
                    .toList();
        } else if ("rate".equalsIgnoreCase(filter.getSortBy())) {
            foods = foods.stream()
                    .sorted(Comparator.comparing(FoodEntity::getRate,
                            "desc".equalsIgnoreCase(filter.getSortDirection())
                                    ? Comparator.reverseOrder() : Comparator.naturalOrder()))
                    .toList();
        }
        return foodMapper.toShortDtoList(foods);
    }

    public FoodDetailsResponse getFoodDetails(UUID id) {
        FoodDetailsDto foodDetailsDto = foodRepository.findById(id)
                .map(foodMapper::toDetailsDto)
                .orElseThrow(() -> new UsernameNotFoundException("Food not found"));
        double rateAmount = ratingService.countRatingAmountForConcreteFood(id);
        boolean couldRate = ratingService.couldRateConcreteFood(id);
        boolean hasRate = ratingService.hasRateFromConcreteUser(id);
        int userRating = ratingService.getUsersRating(id);
        foodDetailsDto.setRate(rateAmount);
        return FoodDetailsResponse.builder()
                .foodDetails(foodDetailsDto)
                .couldRate(couldRate)
                .hasRate(hasRate)
                .userRating(userRating)
                .build();
    }

    public FoodDetailsDto createFood(FoodCreateDto dto) {
        FoodEntity entity = foodMapper.toEntity(dto);
        entity.setIsAvailable(true);
        entity.setCategory(categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new UsernameNotFoundException("Category not found")));
        return foodMapper.toDetailsDto(foodRepository.save(entity));
    }

    @Transactional
    public FoodDetailsDto updateFood(UUID id, FoodUpdateDto dto) {
        FoodEntity entity = foodRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Food not found"));
        foodMapper.updateEntityFromDto(dto, entity);

        if (dto.getCategoryId() != null) {
            CategoryEntity category = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new UsernameNotFoundException("Category not found"));
            entity.setCategory(category);
        }

        if (dto.getIsAvailable() != null)
            entity.setIsAvailable(dto.getIsAvailable());

        FoodDetailsDto foodDetailsDto = foodMapper.toDetailsDto(foodRepository.save(entity));
        double rateAmount = ratingService.countRatingAmountForConcreteFood(id);
        foodDetailsDto.setRate(rateAmount);
        return foodDetailsDto;
    }

    public void deleteFood(UUID id) {
        if (!foodRepository.existsById(id))
            throw new UsernameNotFoundException("Food not found");
        foodRepository.deleteById(id);
        ratingService.deleteRatingByFood(id);
    }

    @Transactional
    public FoodDetailsDto setAvailability(UUID id, boolean available) {
        FoodEntity entity = foodRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Food not found"));
        entity.setIsAvailable(available);
        FoodDetailsDto foodDetailsDto = foodMapper.toDetailsDto(foodRepository.save(entity));
        double rateAmount = ratingService.countRatingAmountForConcreteFood(id);
        foodDetailsDto.setRate(rateAmount);
        return foodDetailsDto;
    }
}
