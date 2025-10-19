package com.example.demo.mappers;

import com.example.demo.dtos.*;
import com.example.demo.entities.CategoryEntity;
import org.mapstruct.Mapper;
import org.mapstruct.MappingTarget;

import java.util.List;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategoryEntity toEntity(CategoryCreateDto dto);
    Category toDto(CategoryEntity entity);
    void updateEntityFromDto(CategoryUpdateDto dto, @MappingTarget CategoryEntity entity);
    List<Category> toDtoList(List<CategoryEntity> entities);
}


