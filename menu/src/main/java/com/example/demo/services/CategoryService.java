package com.example.demo.services;

import com.example.demo.dtos.*;
import com.example.demo.entities.CategoryEntity;
import com.example.demo.repositories.CategoryRepository;
import com.example.demo.repositories.FoodRepository;
import com.example.demo.mappers.CategoryMapper;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class CategoryService {

    private final CategoryRepository categoryRepository;
    private final FoodRepository foodRepository;
    private final CategoryMapper categoryMapper;

    public List<Category> getAllCategories() {
        return categoryMapper.toDtoList(categoryRepository.findAll());
    }

    public Category getCategory(UUID id) {
        CategoryEntity entity = categoryRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Category not found"));
        return categoryMapper.toDto(entity);
    }

    @Transactional
    public Category createCategory(CategoryCreateDto dto) {
        CategoryEntity entity = categoryMapper.toEntity(dto);
        return categoryMapper.toDto(categoryRepository.save(entity));
    }

    @Transactional
    public Category updateCategory(UUID id, CategoryUpdateDto dto) {
        CategoryEntity entity = categoryRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Category not found"));
        categoryMapper.updateEntityFromDto(dto, entity);
        return categoryMapper.toDto(categoryRepository.save(entity));
    }

    @Transactional
    public void deleteCategory(UUID id) throws BadRequestException {
        CategoryEntity entity = categoryRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("Category not found"));
        boolean hasFoods = foodRepository.existsByCategory(entity);
        if (hasFoods) {
            throw new BadRequestException("Cannot delete category with associated foods");
        }
        categoryRepository.delete(entity);
    }
}

