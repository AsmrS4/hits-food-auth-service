package com.example.demo.mappers;

import com.example.demo.dtos.*;
import com.example.demo.entities.*;
import org.mapstruct.Mapper;

@Mapper(componentModel = "spring")
public interface CategoryMapper {
    CategoryEntity toEntity(Category dto);
    Category toDto(CategoryEntity entity);
}

