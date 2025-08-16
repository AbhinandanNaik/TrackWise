package com.company.trackwise.controller;

import com.company.trackwise.model.Employee;
import com.company.trackwise.service.EmployeeService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.Optional;
import java.util.UUID;

@RestController
@RequestMapping("/api/employees")
@RequiredArgsConstructor
public class EmployeeController {

  private final EmployeeService employeeService;

  // Create a new employee
  @PostMapping
  public ResponseEntity<Employee> createEmployee(@RequestBody Employee employee) {
    Employee created = employeeService.create(employee);
    return new ResponseEntity<>(created, HttpStatus.CREATED);
  }

  // Get employee by ID
  @GetMapping("/{id}")
  public ResponseEntity<Employee> getEmployee(@PathVariable UUID id) {
    Employee employee = employeeService.getById(id);
    return ResponseEntity.ok(employee);
  }

  // List employees with pagination
  @GetMapping
  public ResponseEntity<Page<Employee>> listEmployees(Pageable pageable) {
    Page<Employee> employees = employeeService.list(pageable);
    return ResponseEntity.ok(employees);
  }

  // Update employee details
  @PutMapping("/{id}")
  public ResponseEntity<Employee> updateEmployee(@PathVariable UUID id, @RequestBody Employee employee) {
    Employee updated = employeeService.update(id, employee);
    return ResponseEntity.ok(updated);
  }

  // Delete an employee
  @DeleteMapping("/{id}")
  public ResponseEntity<Void> deleteEmployee(@PathVariable UUID id) {
    employeeService.delete(id);
    return ResponseEntity.noContent().build();
  }

  // Find employee by email
  @GetMapping("/search")
  public ResponseEntity<Employee> findByEmail(@RequestParam String email) {
    Optional<Employee> employee = employeeService.findByEmail(email);
    return employee.map(ResponseEntity::ok)
      .orElse(ResponseEntity.notFound().build());
  }
}
