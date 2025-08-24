package org.godigit.trackwise.service.impl;

import lombok.RequiredArgsConstructor;
import org.godigit.trackwise.dto.DepartmentRequest;
import org.godigit.trackwise.dto.DepartmentResponse;
import org.godigit.trackwise.exception.NotFoundException;
import org.godigit.trackwise.mapper.DepartmentMapper;
import org.godigit.trackwise.model.Department;
import org.godigit.trackwise.repository.DepartmentRepository;
import org.godigit.trackwise.service.DepartmentService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;

/**
 * Service implementation for managing the business logic for Department entities.
 * This class adheres to the Single Responsibility Principle by focusing solely
 * on department-related operations.
 */
@Service
@RequiredArgsConstructor // Lombok: Creates a constructor with all final fields.
@Transactional // Ensures all public methods run inside a database transaction.
public class DepartmentServiceImpl implements DepartmentService {

    // The repository for database communication, injected via the constructor.
    private final DepartmentRepository departmentRepository;

    /**
     * Creates a new department based on the provided request data.
     * @param request The DTO containing the data for the new department.
     * @return A DTO representing the newly created department.
     */
    @Override
    public DepartmentResponse create(DepartmentRequest request) {
        // Create a new Department entity.
        Department department = new Department();
        // Populate the entity with data from the request DTO.
        department.setName(request.getName());
        department.setLocation(request.getLocation());
        // Save the new entity to the database.
        Department saved = departmentRepository.save(department);
        // Convert the saved entity to a DTO for the API response.
        return DepartmentMapper.toDto(saved);
    }

    /**
     * Retrieves a single department by its unique ID.
     * @param id The UUID of the department.
     * @return A DTO representing the found department.
     */
    @Override
    @Transactional(readOnly = true) // A performance optimization for read-only queries.
    public DepartmentResponse getById(UUID id) {
        // Find the department by ID, map it to a DTO, or throw an exception if not found.
        return departmentRepository.findById(id)
                .map(DepartmentMapper::toDto)
                .orElseThrow(() -> new NotFoundException("Department not found: " + id));
    }

    /**
     * Retrieves a paginated list of all departments.
     * @param pageable Pagination information.
     * @return A paginated list of department DTOs.
     */
    @Override
    @Transactional(readOnly = true)
    public Page<DepartmentResponse> list(Pageable pageable) {
        // Fetch the page of entities and map it directly to a page of DTOs.
        return departmentRepository.findAll(pageable).map(DepartmentMapper::toDto);
    }

    /**
     * Updates an existing department with new information.
     * @param id The UUID of the department to update.
     * @param request The DTO containing the new data.
     * @return A DTO representing the updated department.
     */
    @Override
    public DepartmentResponse update(UUID id, DepartmentRequest request) {
        // Find the existing department in the database, or throw an exception.
        Department existing = departmentRepository.findById(id)
                .orElseThrow(() -> new NotFoundException("Department not found: " + id));

        // Update the entity's fields from the DTO.
        existing.setName(request.getName());
        existing.setLocation(request.getLocation());

        // Save the updated entity.
        Department updated = departmentRepository.save(existing);

        // Return the DTO representation of the updated department.
        return DepartmentMapper.toDto(updated);
    }

    /**
     * Deletes a department from the system.
     * @param id The UUID of the department to delete.
     */
    @Override
    public void delete(UUID id) {
        // First, check if the department exists to provide a clear error message.
        if (!departmentRepository.existsById(id)) {
            throw new NotFoundException("Department not found: " + id);
        }
        // Delete the department from the database.
        departmentRepository.deleteById(id);
    }
}