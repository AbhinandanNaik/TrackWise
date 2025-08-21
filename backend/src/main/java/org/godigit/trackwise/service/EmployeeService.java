package org.godigit.trackwise.service;

import org.godigit.trackwise.dto.EmployeeRequest;
import org.godigit.trackwise.model.Employee;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface EmployeeService {
  Employee create(EmployeeRequest dto);
  Employee getById(UUID id);
  Page<Employee> list(Pageable pageable);
  Employee update(UUID id, EmployeeRequest dto);
  void delete(UUID id);
  Optional<Employee> findByEmail(String email);
  Employee assignDepartment(UUID employeeId, UUID departmentId);
}
