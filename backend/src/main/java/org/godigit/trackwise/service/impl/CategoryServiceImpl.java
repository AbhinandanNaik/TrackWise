package org.godigit.trackwise.service.impl;

import lombok.RequiredArgsConstructor;
import org.godigit.trackwise.dto.CategoryRequest;
import org.godigit.trackwise.dto.CategoryResponse;
import org.godigit.trackwise.exception.NotFoundException;
import org.godigit.trackwise.mapper.CategoryMapper;
import org.godigit.trackwise.model.AssetCategory;
import org.godigit.trackwise.repository.AssetCategoryRepository;
import org.godigit.trackwise.service.CategoryService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Service implementation for managing the business logic for AssetCategory entities.
 * This class adheres to the Single Responsibility Principle by focusing solely
 * on category-related operations.
 */
@Service
@RequiredArgsConstructor // Lombok: Creates a constructor with all final fields.
@Transactional // Ensures all public methods run inside a database transaction.
public class CategoryServiceImpl implements CategoryService {

    // The repository for database communication, injected via the constructor.
    private final AssetCategoryRepository categoryRepository;

    /**
     * Creates a new asset category based on the provided request data.
     * @param request The DTO containing the data for the new category.
     * @return A DTO representing the newly created category.
     */
    @Override
    public CategoryResponse create(CategoryRequest request) {
        // Create a new AssetCategory entity.
        AssetCategory category = new AssetCategory();
        // Populate the entity with data from the request DTO.
        category.setName(request.getName());
        category.setDescription(request.getDescription());
        // Save the new entity to the database.
        AssetCategory saved = categoryRepository.save(category);
        // Convert the saved entity to a DTO for the API response.
        return CategoryMapper.toDto(saved);
    }

    /**
     * Retrieves a single asset category by its unique ID.
     * @param id The UUID of the category.
     * @return A DTO representing the found category.
     */
    @Override
    @Transactional(readOnly = true) // A performance optimization for read-only queries.
    public CategoryResponse getById(UUID id) {
        // Find the category by ID, map it to a DTO, or throw an exception if not found.
        return categoryRepository.findById(id)
                .map(CategoryMapper::toDto)
                .orElseThrow(() -> new NotFoundException("Asset Category not found: " + id));
    }

    /**
     * Retrieves a paginated list of all asset categories.
     * @param pageable Pagination information.
     * @return A paginated list of category DTOs.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<CategoryResponse> list(Pageable pageable) {
        // Fetch the page of entities and map it directly to a page of DTOs.
        return categoryRepository.findAll(pageable).map(CategoryMapper::toDto);
    }

    /**
     * Updates an existing asset category with new information.
     * @param id The UUID of the category to update.
     * @param request The DTO containing the new data.
     * @return A DTO representing the updated category.
     */
    @Override
    public CategoryResponse update(UUID id, CategoryRequest request) {
        // Find the existing category in the database, or throw an exception.
        AssetCategory existing = categoryRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Asset Category not found: " + id));

        // Update the entity's fields from the DTO.
        existing.setName(request.getName());
        existing.setDescription(request.getDescription());

        // Save the updated entity.
        AssetCategory updated = categoryRepository.save(existing);

        // Return the DTO representation of the updated category.
        return CategoryMapper.toDto(updated);
    }

    /**
     * Deletes an asset category from the system.
     * @param id The UUID of the category to delete.
     */
    @Override
    public void delete(UUID id) {
        // First, check if the category exists to provide a clear error message.
        if (!categoryRepository.existsById(id)) {
            throw new NotFoundException("Asset Category not found: " + id);
        }
        // Delete the category from the database.
        categoryRepository.deleteById(id);
    }
}