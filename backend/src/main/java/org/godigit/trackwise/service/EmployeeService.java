package org.godigit.trackwise.service;

import org.godigit.trackwise.dto.EmployeeRequest;
import org.godigit.trackwise.dto.EmployeeResponse; // Import the Response DTO
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.Optional;
import java.util.UUID;

public interface EmployeeService {

  EmployeeResponse create(EmployeeRequest dto);

  EmployeeResponse getById(UUID id);

  Page<EmployeeResponse> list(Pageable pageable);

  EmployeeResponse update(UUID id, EmployeeRequest dto);

  void delete(UUID id);

  Optional<EmployeeResponse> findByEmail(String email);

  EmployeeResponse assignDepartment(UUID employeeId, UUID departmentId);

  Page<EmployeeResponse> findByDepartment(UUID departmentId, Pageable pageable);

  Page<EmployeeResponse> findByName(String name, Pageable pageable);
}