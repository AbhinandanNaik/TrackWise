package org.godigit.trackwise.mapper;

import org.godigit.trackwise.dto.CategoryResponse;
import org.godigit.trackwise.model.AssetCategory;

/**
 * A utility class to map between AssetCategory entities and DTOs.
 */
public class CategoryMapper {

    /**
     * Converts an AssetCategory entity to a CategoryResponse DTO.
     * @param category The AssetCategory entity to convert.
     * @return The resulting CategoryResponse DTO.
     */
    public static CategoryResponse toDto(AssetCategory category) {
        if (category == null) {
            return null;
        }
        CategoryResponse dto = new CategoryResponse();
        dto.setId(category.getId());
        dto.setName(category.getName());
        dto.setDescription(category.getDescription());
        return dto;
    }
}