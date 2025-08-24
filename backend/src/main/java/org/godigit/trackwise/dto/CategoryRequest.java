package org.godigit.trackwise.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Data Transfer Object for creating or updating an Asset Category.
 */
@Data
public class CategoryRequest {

    @NotBlank(message = "Category name cannot be blank")
    @Size(max = 100)
    private String name;

    @Size(max = 255)
    private String description;
}