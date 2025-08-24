package org.godigit.trackwise.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.godigit.trackwise.dto.DepartmentRequest;
import org.godigit.trackwise.dto.DepartmentResponse;
import org.godigit.trackwise.service.DepartmentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST controller for managing all department-related operations.
 * This controller adheres to the Single Responsibility Principle by focusing
 * only on the CRUD lifecycle of departments.
 */
@RestController
@RequestMapping("/api/departments")
@RequiredArgsConstructor // Lombok: Creates a constructor with all final fields.
@Tag(name = "Department Management", description = "Endpoints for managing departments.")
public class DepartmentController {

    // The service layer that contains the business logic for departments.
    private final DepartmentService departmentService;

    /**
     * Creates a new department.
     * Accessible only by ADMIN role.
     * @param request The DTO containing the new department's data.
     * @return The created department's data as a DTO.
     */
    @PostMapping
    @Operation(summary = "Create a new department")
    public ResponseEntity<DepartmentResponse> createDepartment(@Valid @RequestBody DepartmentRequest request) {
        // Delegate the creation logic to the service layer.
        DepartmentResponse createdDto = departmentService.create(request);
        // Return a 201 CREATED status with the new department's data.
        return new ResponseEntity<>(createdDto, HttpStatus.CREATED);
    }

    /**
     * Retrieves a single department by its unique ID.
     * Accessible by USER and ADMIN roles.
     * @param id The UUID of the department.
     * @return The department's data as a DTO.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get a department by ID")
    public ResponseEntity<DepartmentResponse> getDepartment(@PathVariable UUID id) {
        // Delegate the lookup to the service layer.
        return ResponseEntity.ok(departmentService.getById(id));
    }

    /**
     * Retrieves a paginated list of all departments.
     * Accessible by USER and ADMIN roles.
     * @param pageable Pagination information.
     * @return A paginated list of department DTOs.
     */
    @GetMapping
    @Operation(summary = "List all departments")
    public ResponseEntity<Page<DepartmentResponse>> listDepartments(Pageable pageable) {
        // Delegate the listing logic to the service layer.
        return ResponseEntity.ok(departmentService.list(pageable));
    }

    /**
     * Updates an existing department.
     * Accessible only by ADMIN role.
     * @param id The UUID of the department to update.
     * @param request The DTO with the new data.
     * @return The updated department's data as a DTO.
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update an existing department")
    public ResponseEntity<DepartmentResponse> updateDepartment(@PathVariable UUID id, @Valid @RequestBody DepartmentRequest request) {
        // Delegate the update logic to the service layer.
        return ResponseEntity.ok(departmentService.update(id, request));
    }

    /**
     * Deletes a department.
     * Accessible only by ADMIN role.
     * @param id The UUID of the department to delete.
     * @return A no-content response.
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete a department")
    public ResponseEntity<Void> deleteDepartment(@PathVariable UUID id) {
        // Delegate the deletion to the service layer.
        departmentService.delete(id);
        // Return a 204 No Content status to indicate success.
        return ResponseEntity.noContent().build();
    }
}