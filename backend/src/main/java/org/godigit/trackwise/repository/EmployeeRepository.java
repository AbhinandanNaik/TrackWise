package org.godigit.trackwise.repository;

import org.godigit.trackwise.model.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface EmployeeRepository extends JpaRepository<Employee, UUID> {

  Optional<Employee> findByEmail(String email);

  // Finds all employees for a given department, with pagination
  Page<Employee> findByDepartmentId(UUID departmentId, Pageable pageable);

  Page<Employee> findByFirstNameContainingIgnoreCaseOrLastNameContainingIgnoreCase(String firstName, String lastName, Pageable pageable);

}
