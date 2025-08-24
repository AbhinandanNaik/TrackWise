package org.godigit.trackwise.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;

/**
 * Data Transfer Object for creating or updating a Department.
 * It contains the data that a client is expected to provide.
 */
@Data
public class DepartmentRequest {

    // The name of the department. It cannot be blank.
    @NotBlank(message = "Department name cannot be blank")
    @Size(max = 100)
    private String name;

    // The physical location of the department.
    @Size(max = 255)
    private String location;
}