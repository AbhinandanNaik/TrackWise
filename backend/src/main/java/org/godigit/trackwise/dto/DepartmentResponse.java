package org.godigit.trackwise.dto;

import lombok.Data;
import java.util.UUID;

/**
 * Data Transfer Object for returning Department information.
 * This is a safe representation of the Department entity for API responses.
 */
@Data
public class DepartmentResponse {
    private UUID id;
    private String name;
    private String location;
}