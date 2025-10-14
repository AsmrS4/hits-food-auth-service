package com.example.demo.mappers;

import com.example.demo.dtos.*;
import com.example.demo.entities.*;
import com.example.demo.models.Ingredient;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.Named;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Mapper(componentModel = "spring")
public interface FoodMapper {
    FoodEntity toEntity(Food dto);
    Food toDto(FoodEntity entity);

    @Mapping(target="categoryId", source="category.id")
    FoodShortDto toShortDto(FoodEntity entity);

    @Mapping(target = "categoryId", source = "category.id")
    @Mapping(target = "ingredients", source = "ingredientIds", qualifiedByName = "mapIngredients")
    FoodDetailsDto toDetailsDto(FoodEntity entity);

    @Mapping(target="categoryId", source="category.id")
    List<FoodShortDto> toShortDtoList(List<FoodEntity> entities);


    @Named("mapIngredients")
    default List<Ingredient> mapIngredients(List<Ingredient> ingredientIds) {
        return ingredientIds == null ? Collections.emptyList() : new ArrayList<>(ingredientIds);
    }
}

