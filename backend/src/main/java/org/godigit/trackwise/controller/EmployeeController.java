package org.godigit.trackwise.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.godigit.trackwise.dto.EmployeeRequest;
import org.godigit.trackwise.dto.EmployeeResponse;
import org.godigit.trackwise.service.EmployeeService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

/**
 * REST controller for managing all employee-related operations.
 * This includes creating, retrieving, updating, and deleting employee profiles.
 */
@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
@Tag(name = "3. Employee Management", description = "Endpoints for managing employees.")
public class EmployeeController {

    private final EmployeeService employeeService;

    /**
     * Creates a new employee.
     * Accessible only by ADMIN role.
     * @param requestDTO The DTO containing the new employee's data.
     * @return The created employee's data as a DTO.
     */
    @PostMapping
    @Operation(summary = "Create a new employee")
    public ResponseEntity<EmployeeResponse> createEmployee(@Valid @RequestBody EmployeeRequest requestDTO) {
        // The service now directly returns the response DTO.
        EmployeeResponse createdDto = employeeService.create(requestDTO);
        return new ResponseEntity<>(createdDto, HttpStatus.CREATED);
    }

    /**
     * Retrieves a single employee by their unique ID.
     * Accessible by USER and ADMIN roles.
     * @param id The UUID of the employee.
     * @return The employee's data as a DTO.
     */
    @GetMapping("/{id}")
    @Operation(summary = "Get an employee by ID")
    public ResponseEntity<EmployeeResponse> getEmployee(@PathVariable UUID id) {
        // The service handles the lookup and mapping.
        return ResponseEntity.ok(employeeService.getById(id));
    }

    /**
     * Retrieves a paginated list of all employees.
     * Accessible by USER and ADMIN roles.
     * @param pageable Pagination information.
     * @return A paginated list of employee DTOs.
     */
    @GetMapping
    @Operation(summary = "List all employees with pagination")
    public ResponseEntity<Page<EmployeeResponse>> listEmployees(Pageable pageable) {
        // The service returns a Page of DTOs, no mapping needed here.
        return ResponseEntity.ok(employeeService.list(pageable));
    }

    /**
     * Updates an existing employee's profile.
     * Accessible only by ADMIN role.
     * @param id The UUID of the employee to update.
     * @param requestDTO The DTO with the new data.
     * @return The updated employee's data as a DTO.
     */
    @PutMapping("/{id}")
    @Operation(summary = "Update an existing employee")
    public ResponseEntity<EmployeeResponse> updateEmployee(@PathVariable UUID id,
                                                           @Valid @RequestBody EmployeeRequest requestDTO) {
        return ResponseEntity.ok(employeeService.update(id, requestDTO));
    }

    /**
     * Deletes an employee from the system.
     * Accessible only by ADMIN role.
     * @param id The UUID of the employee to delete.
     * @return A no-content response.
     */
    @DeleteMapping("/{id}")
    @Operation(summary = "Delete an employee")
    public ResponseEntity<Void> deleteEmployee(@PathVariable UUID id) {
        employeeService.delete(id);
        return ResponseEntity.noContent().build();
    }

    /**
     * Finds a single employee by their email address.
     * Accessible by USER and ADMIN roles.
     * @param email The email to search for.
     * @return The found employee's data, or 404 Not Found.
     */
    @GetMapping("/search")
    @Operation(summary = "Find an employee by email")
    public ResponseEntity<EmployeeResponse> findByEmail(@RequestParam String email) {
        return employeeService.findByEmail(email)
                .map(ResponseEntity::ok) // If found, wrap in ResponseEntity.ok()
                .orElse(ResponseEntity.notFound().build()); // If not found, return 404.
    }

    /**
     * Finds all employees belonging to a specific department, with pagination.
     * Accessible by USER and ADMIN roles.
     *
     * @param departmentId The UUID of the department to filter by.
     * @param pageable Pagination information (page, size, sort).
     * @return A paginated list of employees in the specified department.
     */
    @GetMapping("/by-department/{departmentId}")
    @Operation(summary = "Find all employees in a department")
    public ResponseEntity<Page<EmployeeResponse>> findByDepartment(
            @PathVariable UUID departmentId,
            Pageable pageable) {
        // The service call is straightforward and returns the paginated DTO list.
        return ResponseEntity.ok(employeeService.findByDepartment(departmentId, pageable));
    }

    /**
     * Finds employees whose first or last name contains the given search term.
     * This search is case-insensitive and supports pagination.
     * Accessible by USER and ADMIN roles.
     *
     * @param name The search term for the employee's name.
     * @param pageable Pagination information.
     * @return A paginated list of employees matching the name.
     */
    @GetMapping("/search/by-name")
    @Operation(summary = "Find employees by name")
    public ResponseEntity<Page<EmployeeResponse>> findByName(
            @RequestParam String name,
            Pageable pageable) {
        return ResponseEntity.ok(employeeService.findByName(name, pageable));
    }

    /**
     * Assigns an employee to a specific department.
     * This is an administrative action.
     * Accessible only by ADMIN role.
     * @param employeeId The UUID of the employee to assign.
     * @param departmentId The UUID of the new department.
     * @return The updated employee's data as a DTO.
     */
    @PutMapping("/{employeeId}/assign-department")
    @Operation(summary = "Assign an employee to a department")
    public ResponseEntity<EmployeeResponse> assignDepartment(
            @PathVariable UUID employeeId,
            @RequestParam UUID departmentId) {
        // Delegate the business logic to the EmployeeService.
        EmployeeResponse updatedEmployee = employeeService.assignDepartment(employeeId, departmentId);
        return ResponseEntity.ok(updatedEmployee);
    }


}