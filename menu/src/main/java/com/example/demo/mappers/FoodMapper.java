package com.example.demo.mappers;

import com.example.demo.dtos.*;
import com.example.demo.entities.*;
import com.example.demo.models.Ingredient;
import org.mapstruct.*;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Mapper(componentModel = "spring")
public interface FoodMapper {
    FoodEntity toEntity(Food dto);

    @Mapping(target = "category", ignore = true)
    @Mapping(target = "ingredientIds", source = "ingredients")
    @Mapping(target = "photos", source = "photos")
    FoodEntity toEntity(FoodCreateDto dto);

    Food toDto(FoodEntity entity);

    @Mapping(target="categoryId", source="category.id")
    @Mapping(target = "photos", source = "photos")
    FoodShortDto toShortDto(FoodEntity entity);

    @Mapping(target = "categoryId", source = "category.id")
    @Mapping(target = "ingredients", source = "ingredientIds", qualifiedByName = "mapIngredients")
    @Mapping(target = "photos", source = "photos")
    FoodDetailsDto toDetailsDto(FoodEntity entity);

    @Mapping(target="categoryId", source="category.id")
    @Mapping(target = "photos", source = "photos")
    List<FoodShortDto> toShortDtoList(List<FoodEntity> entities);

    @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
    @Mapping(target = "photos", ignore = true)
    @Mapping(target = "category", ignore = true)
    @Mapping(target = "ingredientIds", ignore = true)
    void updateEntityFromDto(FoodUpdateDto dto, @MappingTarget FoodEntity entity);

    @Named("mapIngredients")
    default List<Ingredient> mapIngredients(List<Ingredient> ingredientIds) {
        return ingredientIds == null ? Collections.emptyList() : new ArrayList<>(ingredientIds);
    }

    @AfterMapping
    default void linkCategory(@MappingTarget FoodEntity entity, FoodCreateDto dto) {
        if (dto.getCategoryId() != null) {
            CategoryEntity category = new CategoryEntity();
            category.setId(dto.getCategoryId());
            entity.setCategory(category);
        }
    }
}

