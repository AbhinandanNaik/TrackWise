package org.godigit.trackwise.service.impl;

import lombok.RequiredArgsConstructor;
import org.godigit.trackwise.dto.EmployeeRequest;
import org.godigit.trackwise.dto.EmployeeResponse;
import org.godigit.trackwise.exception.DuplicateResourceException;
import org.godigit.trackwise.exception.NotFoundException;
import org.godigit.trackwise.mapper.EmployeeMapper;
import org.godigit.trackwise.model.Department;
import org.godigit.trackwise.model.Employee;
import org.godigit.trackwise.repository.DepartmentRepository;
import org.godigit.trackwise.repository.EmployeeRepository;
import org.godigit.trackwise.service.EmployeeService;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.Optional;
import java.util.UUID;

/**
 * Service implementation for managing the core lifecycle of employees.
 * This class contains the business logic for creating, retrieving, updating,
 * and deleting employee profiles.
 */
@Service
@RequiredArgsConstructor
@Transactional
public class EmployeeServiceImpl implements EmployeeService {

  private final EmployeeRepository employeeRepository;
  private final DepartmentRepository departmentRepository;

  /**
   * Creates a new employee from a request DTO.
   * @param dto The DTO containing the new employee's data.
   * @return A DTO representing the newly created employee.
   */
  @Override
  public EmployeeResponse create(EmployeeRequest dto) {
    if (employeeRepository.existsByEmail(dto.getEmail())) {
      throw new DuplicateResourceException("An employee with the email '" + dto.getEmail() + "' already exists.");
    }
    // Find the department to link to the employee.
    Department dept = findDepartmentById(dto.getDepartmentId());

    // Convert the DTO to an Employee entity.
    Employee emp = EmployeeMapper.toEntity(dto, dept);

    // Save the new entity to the database.
    Employee saved = employeeRepository.save(emp);

    // Convert the saved entity back to a response DTO.
    return EmployeeMapper.toDto(saved);
  }

  /**
   * Retrieves a single employee by their unique ID.
   * @param id The UUID of the employee.
   * @return A DTO representing the found employee.
   */
  @Override
  @Transactional(readOnly = true)
  public EmployeeResponse getById(UUID id) {
    return employeeRepository.findById(id)
            .map(EmployeeMapper::toDto)
            .orElseThrow(() -> new NotFoundException("Employee not found: " + id));
  }

  /**
   * Retrieves a paginated list of all employees.
   * @param pageable Pagination information.
   * @return A paginated list of employee DTOs.
   */
  @Override
  @Transactional(readOnly = true)
  public Page<EmployeeResponse> list(Pageable pageable) {
    // Fetch the page of entities and map it to a page of DTOs.
    return employeeRepository.findAll(pageable).map(EmployeeMapper::toDto);
  }

  /**
   * Updates an existing employee's profile.
   * @param id The UUID of the employee to update.
   * @param dto The DTO with the new data.
   * @return The updated employee's data as a DTO.
   */
  @Override
  public EmployeeResponse update(UUID id, EmployeeRequest dto) {
    // Find the existing employee in the database.
    Employee existing = employeeRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Employee not found: " + id));

    // Find the department to link.
    Department dept = findDepartmentById(dto.getDepartmentId());

    // Update the existing entity's fields from the DTO.
    existing.setFirstName(dto.getFirstName());
    existing.setLastName(dto.getLastName());
    existing.setEmail(dto.getEmail());
    existing.setPhone(dto.getPhone());
    existing.setDepartment(dept);

    // Save the updated entity and return the DTO representation.
    Employee updated = employeeRepository.save(existing);
    return EmployeeMapper.toDto(updated);
  }

  /**
   * Deletes an employee from the system.
   * @param id The UUID of the employee to delete.
   */
  @Override
  public void delete(UUID id) {
    if (!employeeRepository.existsById(id)) {
      throw new NotFoundException("Employee not found: " + id);
    }
    employeeRepository.deleteById(id);
  }

  /**
   * Finds a single employee by their email address.
   * @param email The email to search for.
   * @return An Optional containing the found employee's DTO, or empty if not found.
   */
  @Override
  @Transactional(readOnly = true)
  public Optional<EmployeeResponse> findByEmail(String email) {
    return employeeRepository.findByEmail(email).map(EmployeeMapper::toDto);
  }

  /**
   * Assigns an employee to a different department. This is a key administrative
   * function for managing team structures.
   *
   * @param employeeId   The UUID of the employee to assign.
   * @param departmentId The UUID of the new department.
   * @return The updated employee's data as a DTO.
   */
  @Override
  public EmployeeResponse assignDepartment(UUID employeeId, UUID departmentId) {
    // Retrieve the existing employee record from the database.
    Employee employee = employeeRepository.findById(employeeId)
            .orElseThrow(() -> new NotFoundException("Employee not found: " + employeeId));

    // Use the helper method to find the new department.
    Department department = findDepartmentById(departmentId);

    // Set the new department on the employee object.
    employee.setDepartment(department);

    // Save the changes to the database.
    Employee saved = employeeRepository.save(employee);

    // Convert the updated entity to a DTO for the response.
    return EmployeeMapper.toDto(saved);
  }

  /**
   * A private helper method to find a Department by its ID.
   * This reduces code duplication within the service.
   *
   * @param departmentId The UUID of the department to find.
   * @return The found Department entity, or null if the ID is null.
   */
  private Department findDepartmentById(UUID departmentId) {
    // If no ID is provided, there's no department to find.
    if (departmentId == null) {
      return null;
    }
    // Find the department or throw a NotFoundException if it doesn't exist.
    return departmentRepository.findById(departmentId)
            .orElseThrow(() -> new NotFoundException("Department not found: " + departmentId));
  }

  /**
   * Finds all employees belonging to a specific department, with pagination.
   *
   * @param departmentId The UUID of the department to filter by.
   * @param pageable     Pagination information (page, size, sort).
   * @return A paginated list of employees in the specified department.
   */
  @Override
  @Transactional(readOnly = true)
  public Page<EmployeeResponse> findByDepartment(UUID departmentId, Pageable pageable) {
    // Call the repository method and map the resulting Page of entities to DTOs.
    return employeeRepository.findByDepartmentId(departmentId, pageable)
            .map(EmployeeMapper::toDto);
  }

  /**
   * Finds employees whose first or last name contains a search term.
   *
   * @param name     The search term for the employee's name.
   * @param pageable Pagination information.
   * @return A paginated list of employees matching the name.
   */
  @Override
  @Transactional(readOnly = true)
  public Page<EmployeeResponse> findByName(String name, Pageable pageable) {
    // Pass the name to both parameters to search in both first and last names.
    return employeeRepository.findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(name, name, pageable)
            .map(EmployeeMapper::toDto);
  }
}