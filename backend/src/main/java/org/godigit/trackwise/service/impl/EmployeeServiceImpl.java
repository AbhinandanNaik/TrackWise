package org.godigit.trackwise.service.impl;

import lombok.RequiredArgsConstructor;
import org.godigit.trackwise.dto.EmployeeRequest;
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

@Service
@RequiredArgsConstructor
@Transactional
public class EmployeeServiceImpl implements EmployeeService {

  private final EmployeeRepository employeeRepository;
  private final DepartmentRepository departmentRepository; // ✅ needed to resolve dept

  @Override
  public Employee create(EmployeeRequest dto) {
    Department dept = null;
    if (dto.getDepartmentId() != null) {
      dept = departmentRepository.findById(dto.getDepartmentId())
              .orElseThrow(() -> new NotFoundException("Department not found: " + dto.getDepartmentId()));
    }
    Employee emp = EmployeeMapper.toEntity(dto, dept);
    return employeeRepository.save(emp);
  }

  @Override
  @Transactional(readOnly = true)
  public Employee getById(UUID id) {
    return employeeRepository.findById(id)
            .orElseThrow(() -> new NotFoundException("Employee not found: " + id));
  }

  @Override
  @Transactional(readOnly = true)
  public Page<Employee> list(Pageable pageable) {
    return employeeRepository.findAll(pageable);
  }

  @Override
  public Employee update(UUID id, EmployeeRequest dto) {
    Employee existing = getById(id);

    Department dept = null;
    if (dto.getDepartmentId() != null) {
      dept = departmentRepository.findById(dto.getDepartmentId())
              .orElseThrow(() -> new NotFoundException("Department not found: " + dto.getDepartmentId()));
    }

    existing.setFirstName(dto.getFirstName());
    existing.setLastName(dto.getLastName());
    existing.setEmail(dto.getEmail());
    existing.setPhone(dto.getPhone());
    existing.setDepartment(dept);

    return employeeRepository.save(existing);
  }

  @Override
  public void delete(UUID id) {
    Employee emp = getById(id);
    employeeRepository.delete(emp); // soft delete can be added later
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<Employee> findByEmail(String email) {
    return employeeRepository.findByEmail(email);
  }

  @Override
  public Employee assignDepartment(UUID employeeId, UUID departmentId) {
    Employee employee = employeeRepository.findById(employeeId)
            .orElseThrow(() -> new NotFoundException("Employee not found"));

    Department department = departmentRepository.findById(departmentId)
            .orElseThrow(() -> new NotFoundException("Department not found"));

    employee.setDepartment(department); // Assign the actual Department object

    return employeeRepository.save(employee);
  }
}
