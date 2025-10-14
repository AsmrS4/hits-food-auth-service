package com.example.demo.services;

import com.example.demo.dtos.FoodDetailsDto;
import com.example.demo.dtos.FoodFilterRequest;
import com.example.demo.dtos.FoodShortDto;
import com.example.demo.entities.FoodEntity;
import com.example.demo.mappers.FoodMapper;
import com.example.demo.repositories.FoodRepository;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class FoodService {

    private final FoodRepository foodRepository;
    private final FoodMapper foodMapper;

    public List<FoodShortDto> getAllFoods(FoodFilterRequest filter) {
        List<FoodEntity> foods = foodRepository.findAll();
        if (filter == null) {
            return foodMapper.toShortDtoList(foods);
        }
        log.warn(String.format("RECEIVED FILTERS: {%s}", filter));
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
                            "desc".equalsIgnoreCase(filter.getSortDirection()) ? Comparator.reverseOrder() : Comparator.naturalOrder()))
                    .toList();
        } else if ("rate".equalsIgnoreCase(filter.getSortBy())) {
            foods = foods.stream()
                    .sorted(Comparator.comparing(FoodEntity::getRate,
                            "desc".equalsIgnoreCase(filter.getSortDirection()) ? Comparator.reverseOrder() : Comparator.naturalOrder()))
                    .toList();
        }

        return foodMapper.toShortDtoList(foods);
    }

    public FoodDetailsDto getFoodDetails(UUID id) {
        return foodRepository.findById(id)
                .map(foodMapper::toDetailsDto)
                .orElseThrow(() -> new RuntimeException("Food not found"));
    }
}