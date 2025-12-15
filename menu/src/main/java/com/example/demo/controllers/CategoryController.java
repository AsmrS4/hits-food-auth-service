package com.example.demo.controllers;

import com.example.demo.dtos.*;
import com.example.demo.services.CategoryService;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/categories")
@RequiredArgsConstructor
@SecurityRequirement(name = "bearerAuth")
@Tag(name = "Categories controller")
public class CategoryController {

    private final CategoryService categoryService;

    @GetMapping
    @Operation(
            description = "Getting list of categories",
            summary = "This is summary for getting list of available categories in application"
    )
    public ResponseEntity<List<Category>> getAll() {
        return ResponseEntity.ok(categoryService.getAllCategories());
    }

    @GetMapping("/{id}")
    @Operation(
            description = "Getting a concrete category",
            summary = "This is summary for getting details about category"
    )
    public ResponseEntity<Category> get(@PathVariable UUID id) {
        return ResponseEntity.ok(categoryService.getCategory(id));
    }

    @PostMapping
    @Operation(
            description = "Create a new category",
            summary = "This is summary for adding a new category"
    )
    public ResponseEntity<Category> create(@RequestBody CategoryCreateDto dto) {
        return ResponseEntity.ok(categoryService.createCategory(dto));
    }

    @PutMapping("/{id}")
    @PostMapping
    @Operation(
            description = "Edit a category",
            summary = "This is summary for edit an existing category"
    )
    public ResponseEntity<Category> update(@PathVariable UUID id, @RequestBody CategoryUpdateDto dto) {
        return ResponseEntity.ok(categoryService.updateCategory(id, dto));
    }

    @DeleteMapping("/{id}")
    @Operation(
            description = "Delete a category",
            summary = "This is summary for deleting an existing category"
    )
    public ResponseEntity<Void> delete(@PathVariable UUID id) throws BadRequestException {
        categoryService.deleteCategory(id);
        return ResponseEntity.noContent().build();
    }
}
