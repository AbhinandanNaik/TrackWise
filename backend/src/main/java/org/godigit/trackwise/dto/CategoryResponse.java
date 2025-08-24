package org.godigit.trackwise.dto;

import lombok.Data;
import java.util.UUID;

/**
 * Data Transfer Object for returning Asset Category information.
 */
@Data
public class CategoryResponse {
    private UUID id;
    private String name;
    private String description;
}