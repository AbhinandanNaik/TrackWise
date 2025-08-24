package org.godigit.trackwise.service;

import org.godigit.trackwise.dto.CategoryRequest;
import org.godigit.trackwise.dto.CategoryResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

/**
 * Service interface defining the business operations for Asset Category management.
 */
public interface CategoryService {
    CategoryResponse create(CategoryRequest request);
    CategoryResponse getById(UUID id);
    Page<CategoryResponse> list(Pageable pageable);
    CategoryResponse update(UUID id, CategoryRequest request);
    void delete(UUID id);
}