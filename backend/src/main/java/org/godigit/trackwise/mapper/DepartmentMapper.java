package org.godigit.trackwise.mapper;

import org.godigit.trackwise.dto.DepartmentResponse;
import org.godigit.trackwise.model.Department;

/**
 * A utility class to map between Department entities and DTOs.
 * This helps to decouple the database layer from the API layer.
 */
public class DepartmentMapper {

    /**
     * Converts a Department entity to a DepartmentResponse DTO.
     * @param department The Department entity to convert.
     * @return The resulting DepartmentResponse DTO.
     */
    public static DepartmentResponse toDto(Department department) {
        if (department == null) {
            return null;
        }
        DepartmentResponse dto = new DepartmentResponse();
        dto.setId(department.getId());
        dto.setName(department.getName());
        dto.setLocation(department.getLocation());
        return dto;
    }
}