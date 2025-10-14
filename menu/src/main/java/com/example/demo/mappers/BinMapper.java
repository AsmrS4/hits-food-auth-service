package com.example.demo.mappers;

import com.example.demo.dtos.Bin;
import com.example.demo.entities.BinEntity;
import com.example.demo.entities.FoodEntity;
import com.example.demo.dtos.Food;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;

import java.util.ArrayList;
import java.util.List;

@Mapper(componentModel = "spring", uses = {FoodMapper.class})
public interface BinMapper {

    @Mapping(target = "foodList", source = "foodList")
    Bin toDto(BinEntity entity);

    @Mapping(target = "foodList", source = "foodList")
    BinEntity toEntity(Bin dto);

    default List<Food> mapFoodListToDto(List<FoodEntity> foodEntities, FoodMapper foodMapper) {
        if (foodEntities == null) return new ArrayList<>();
        List<Food> foods = new ArrayList<>();
        foodEntities.forEach(f -> foods.add(foodMapper.toDto(f)));
        return foods;
    }

    default List<FoodEntity> mapFoodListToEntity(List<Food> foods, FoodMapper foodMapper) {
        if (foods == null) return new ArrayList<>();
        List<FoodEntity> entities = new ArrayList<>();
        foods.forEach(f -> entities.add(foodMapper.toEntity(f)));
        return entities;
    }
}
