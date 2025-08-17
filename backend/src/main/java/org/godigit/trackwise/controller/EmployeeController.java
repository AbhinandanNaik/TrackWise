package org.godigit.trackwise.controller;

import lombok.RequiredArgsConstructor;
import org.godigit.trackwise.dto.EmployeeRequestDTO;
import org.godigit.trackwise.dto.EmployeeResponseDTO;
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
    public ResponseEntity<EmployeeResponseDTO> createEmployee(@RequestBody EmployeeRequestDTO requestDTO) {
        Employee created = employeeService.create(requestDTO);
        return new ResponseEntity<>(EmployeeMapper.toDto(created), HttpStatus.CREATED);
    }

    // ✅ Get by ID
    @GetMapping("/{id}")
    public ResponseEntity<EmployeeResponseDTO> getEmployee(@PathVariable UUID id) {
        Employee employee = employeeService.getById(id);
        return ResponseEntity.ok(EmployeeMapper.toDto(employee));
    }

    // ✅ List with pagination
    @GetMapping
    public ResponseEntity<Page<EmployeeResponseDTO>> listEmployees(Pageable pageable) {
        Page<EmployeeResponseDTO> employees = employeeService.list(pageable)
                .map(EmployeeMapper::toDto);
        return ResponseEntity.ok(employees);
    }

    // ✅ Update
    @PutMapping("/{id}")
    public ResponseEntity<EmployeeResponseDTO> updateEmployee(@PathVariable UUID id,
                                                              @RequestBody EmployeeRequestDTO requestDTO) {
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
    public ResponseEntity<EmployeeResponseDTO> findByEmail(@RequestParam String email) {
        Optional<Employee> employeeOpt = employeeService.findByEmail(email);
        return employeeOpt
                .map(employee -> ResponseEntity.ok(EmployeeMapper.toDto(employee)))
                .orElse(ResponseEntity.notFound().build());
    }
}
