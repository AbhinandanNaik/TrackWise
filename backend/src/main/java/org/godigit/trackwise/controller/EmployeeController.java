package org.godigit.trackwise.controller;

import lombok.RequiredArgsConstructor;
import org.godigit.trackwise.dto.EmployeeRequest;
import org.godigit.trackwise.dto.EmployeeResponse;
import org.godigit.trackwise.mapper.EmployeeMapper;
import org.godigit.trackwise.model.Employee;
import org.godigit.trackwise.service.EmployeeService;
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

    // ✅ Create
    @PostMapping
    public ResponseEntity<EmployeeResponse> createEmployee(@RequestBody EmployeeRequest requestDTO) {
        Employee created = employeeService.create(requestDTO);
        return new ResponseEntity<>(EmployeeMapper.toDto(created), HttpStatus.CREATED);
    }

    // ✅ Get by ID
    @GetMapping("/{id}")
    public ResponseEntity<EmployeeResponse> getEmployee(@PathVariable UUID id) {
        Employee employee = employeeService.getById(id);
        return ResponseEntity.ok(EmployeeMapper.toDto(employee));
    }

    // ✅ List with pagination
    @GetMapping
    public ResponseEntity<Page<EmployeeResponse>> listEmployees(Pageable pageable) {
        Page<EmployeeResponse> employees = employeeService.list(pageable)
                .map(EmployeeMapper::toDto);
        return ResponseEntity.ok(employees);
    }

    // ✅ Update
    @PutMapping("/{id}")
    public ResponseEntity<EmployeeResponse> updateEmployee(@PathVariable UUID id,
                                                           @RequestBody EmployeeRequest requestDTO) {
        Employee updated = employeeService.update(id, requestDTO);
        return ResponseEntity.ok(EmployeeMapper.toDto(updated));
    }

    // ✅ Delete
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteEmployee(@PathVariable UUID id) {
        employeeService.delete(id);
        return ResponseEntity.noContent().build();
    }

    // ✅ Find by email
    @GetMapping("/search")
    public ResponseEntity<EmployeeResponse> findByEmail(@RequestParam String email) {
        Optional<Employee> employeeOpt = employeeService.findByEmail(email);
        return employeeOpt
                .map(employee -> ResponseEntity.ok(EmployeeMapper.toDto(employee)))
                .orElse(ResponseEntity.notFound().build());
    }
}
