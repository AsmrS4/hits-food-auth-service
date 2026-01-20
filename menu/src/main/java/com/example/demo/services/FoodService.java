package com.example.demo.services;

import com.example.demo.config.FeatureToggles;
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
    private final FileStorageService fileStorageService;
    private final FeatureToggles features;

    public List<FoodShortDto> getAllFoods(FoodFilterRequest filter) {
        List<FoodEntity> foods = foodRepository.findByIsDeletedFalse();
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
        if (features.isBugIgnoreOnePriceBound()) {
            if (filter.getMinPrice() != null) {
                foods = foods.stream()
                        .filter(f -> f.getPrice() >= filter.getMinPrice())
                        .toList();
            }
        } else {
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
        }
        if (filter.getSearch() != null && !filter.getSearch().isBlank()) {
            String search = filter.getSearch();

            if (features.isBugSearchExactNameOnly()) {
                if (features.isBugNameSearchCaseSensitive()) {
                    foods = foods.stream()
                            .filter(f -> f.getName().equals(search))
                            .toList();
                } else {
                    foods = foods.stream()
                            .filter(f -> f.getName().equalsIgnoreCase(search))
                            .toList();
                }
            } else {
                if (features.isBugNameSearchCaseSensitive()) {
                    foods = foods.stream()
                            .filter(f -> f.getName().contains(search))
                            .toList();
                } else {
                    String searchLower = search.toLowerCase();
                    foods = foods.stream()
                            .filter(f -> f.getName().toLowerCase().contains(searchLower))
                            .toList();
                }
            }
        }
        if ("price".equalsIgnoreCase(filter.getSortBy())) {
            if (features.isBugWrongPriceSorting()) {
                foods = foods.stream()
                        .sorted(Comparator.comparing(FoodEntity::getPrice).reversed())
                        .toList();
            } else {
                foods = foods.stream()
                        .sorted(Comparator.comparing(FoodEntity::getPrice,
                                "desc".equalsIgnoreCase(filter.getSortDirection())
                                        ? Comparator.reverseOrder() : Comparator.naturalOrder()))
                        .toList();
            }
        } else if ("rate".equalsIgnoreCase(filter.getSortBy())) {
            foods = foods.stream()
                    .sorted(Comparator.comparing(FoodEntity::getRate,
                            "desc".equalsIgnoreCase(filter.getSortDirection())
                                    ? Comparator.reverseOrder() : Comparator.naturalOrder()))
                    .toList();
        }
        if (features.isBugDeletedFoodVisible()) {
            foods.addAll(foodRepository.findAll());
        }
        List<FoodShortDto> foodShortDtos = foodMapper.toShortDtoList(foods);
        return foodShortDtos.stream().map(dto -> {
            double amountRating = ratingService.countRatingAmountForConcreteFood(dto.getId());
            dto.setRate(amountRating);
            return dto;
        }).collect(Collectors.toList());
    }

    public FoodDetailsResponse getFoodDetails(UUID id) {
        FoodEntity food = foodRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Food not found"));
        if (food.getIsDeleted()) {
            throw new UsernameNotFoundException("Food not found");
        }
        FoodDetailsDto foodDetailsDto = foodRepository.findById(id)
                .map(foodMapper::toDetailsDto)
                .orElseThrow(() -> new UsernameNotFoundException("Food not found"));
        if (features.isBugDropFoodPhotosOnRead()) {
            foodDetailsDto.setPhotos(new ArrayList<>());
        }
        double rateAmount = ratingService.countRatingAmountForConcreteFood(id);
        boolean couldRate = ratingService.couldRateConcreteFood(id);
        boolean hasRate = ratingService.hasRateFromConcreteUser(id);
        double userRating = ratingService.getUsersRating(id);
        if (features.isBugFoodDetailsWrongUserRating()) {
            userRating = 5.0;
        }
        foodDetailsDto.setRate(rateAmount);
        if (features.isBugDistortFoodPriceOnDetails()) {
            foodDetailsDto.setPrice(foodDetailsDto.getPrice() * 0.9);
        }
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
        entity.setIsDeleted(false);
        entity.setCategory(categoryRepository.findById(dto.getCategoryId())
                .orElseThrow(() -> new UsernameNotFoundException("Category not found")));

        if (dto.getPhotos() != null && !dto.getPhotos().isEmpty()) {
            List<String> photoPaths = fileStorageService.storeFiles(dto.getPhotos());
            entity.setPhotos(photoPaths);
        } else {
            entity.setPhotos(new ArrayList<>());
        }

        return foodMapper.toDetailsDto(foodRepository.save(entity));
    }

    @Transactional
    public FoodDetailsDto updateFood(UUID id, FoodUpdateDto dto) {
        FoodEntity entity = foodRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Food not found"));
        if (entity.getIsDeleted()) {
            throw new UsernameNotFoundException("Food not found");
        }

        List<String> currentPhotos = new ArrayList<>(entity.getPhotos());

        if (dto.getPhotosToDelete() != null) {
            fileStorageService.deleteFiles(dto.getPhotosToDelete());
            currentPhotos.removeAll(dto.getPhotosToDelete());
        }

        if (dto.getNewPhotos() != null && !dto.getNewPhotos().isEmpty()) {
            List<String> newPhotoPaths = fileStorageService.storeFiles(dto.getNewPhotos());
            currentPhotos.addAll(newPhotoPaths);
        }

        if (dto.getExistingPhotos() != null) {
            currentPhotos = new ArrayList<>(dto.getExistingPhotos());
        }

        entity.setPhotos(currentPhotos);

        foodMapper.updateEntityFromDto(dto, entity);

        if (dto.getCategoryId() != null) {
            CategoryEntity category = categoryRepository.findById(dto.getCategoryId())
                    .orElseThrow(() -> new UsernameNotFoundException("Category not found"));
            entity.setCategory(category);
        }

        if (dto.getIngredients() != null) {
            entity.setIngredientIds(dto.getIngredients());
        }

        FoodDetailsDto foodDetailsDto = foodMapper.toDetailsDto(entity);
        double rateAmount = ratingService.countRatingAmountForConcreteFood(id);
        foodDetailsDto.setRate(rateAmount);

        return foodDetailsDto;
    }

    @Transactional
    public void deleteFood(UUID id) {
        FoodEntity food = foodRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Food not found"));
        food.setIsDeleted(true);
        food.setIsAvailable(false);

        foodRepository.save(food);
    }

    @Transactional
    public FoodDetailsDto restoreFood(UUID id) {
        FoodEntity entity = foodRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Food not found"));

        entity.setIsDeleted(false);
        entity.setIsAvailable(true);

        FoodDetailsDto foodDetailsDto = foodMapper.toDetailsDto(foodRepository.save(entity));
        double rateAmount = ratingService.countRatingAmountForConcreteFood(id);
        foodDetailsDto.setRate(rateAmount);
        return foodDetailsDto;
    }

    @Transactional
    public FoodDetailsDto setAvailability(UUID id, boolean available) {
        FoodEntity entity = foodRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Food not found"));
        if (entity.getIsDeleted()) {
            throw new UsernameNotFoundException("Food not found");
        }
        entity.setIsAvailable(available);
        FoodDetailsDto foodDetailsDto = foodMapper.toDetailsDto(foodRepository.save(entity));
        double rateAmount = ratingService.countRatingAmountForConcreteFood(id);
        foodDetailsDto.setRate(rateAmount);
        return foodDetailsDto;
    }
}