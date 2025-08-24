package org.godigit.trackwise.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.godigit.trackwise.dto.CategoryRequest;
import org.godigit.trackwise.dto.CategoryResponse;
import org.godigit.trackwise.service.CategoryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST controller for managing all asset category-related operations.
 * This adheres to the Single Responsibility Principle by focusing only on categories.
 */
@RestController
@RequestMapping("/api/asset-categories")
@RequiredArgsConstructor // Lombok: Creates a constructor with all final fields.
@Tag(name = "Asset Category Management", description = "Endpoints for managing asset categories.")
public class AssetCategoryController {

    private final CategoryService categoryService;

    /**
     * Creates a new asset category.
     * Accessible only by ADMIN role.
     * @param request The DTO containing the new category's data.
     * @return The created category's data as a DTO.
     */
    @PostMapping
    @Operation(summary = "Create a new asset category")
    public ResponseEntity<CategoryResponse> createCategory(@Valid @RequestBody CategoryRequest request) {
        CategoryResponse createdDto = categoryService.create(request);
        return new ResponseEntity<>(createdDto, HttpStatus.CREATED);
    }

    /**
     * Retrieves a single asset category by its unique ID.
     * Accessible by USER and ADMIN roles.
     * @param id The UUID of the category.
     * @return The category's data as a DTO.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get an asset category by ID")
    public ResponseEntity<CategoryResponse> getCategory(@PathVariable UUID id) {
        return ResponseEntity.ok(categoryService.getById(id));
    }

    /**
     * Retrieves a paginated list of all asset categories.
     * Accessible by USER and ADMIN roles.
     * @param pageable Pagination information.
     * @return A paginated list of category DTOs.
     */
    @GetMapping
    @Operation(summary = "List all asset categories")
    public ResponseEntity<Page<CategoryResponse>> listCategories(Pageable pageable) {
        return ResponseEntity.ok(categoryService.list(pageable));
    }

    /**
     * Updates an existing asset category.
     * Accessible only by ADMIN role.
     * @param id The UUID of the category to update.
     * @param request The DTO with the new data.
     * @return The updated category's data as a DTO.
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update an existing asset category")
    public ResponseEntity<CategoryResponse> updateCategory(@PathVariable UUID id, @Valid @RequestBody CategoryRequest request) {
        return ResponseEntity.ok(categoryService.update(id, request));
    }

    /**
     * Deletes an asset category.
     * Accessible only by ADMIN role.
     * @param id The UUID of the category to delete.
     * @return A no-content response.
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an asset category")
    public ResponseEntity<Void> deleteCategory(@PathVariable UUID id) {
        categoryService.delete(id);
        return ResponseEntity.noContent().build();
    }
}