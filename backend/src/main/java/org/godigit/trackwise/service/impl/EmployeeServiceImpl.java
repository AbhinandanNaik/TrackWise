package org.godigit.trackwise.service.impl;


import lombok.RequiredArgsConstructor;
import org.godigit.trackwise.exception.NotFoundException;
import org.godigit.trackwise.model.Employee;
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

  @Override
  public Employee create(Employee employee) {
    return employeeRepository.save(employee);
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
  public Employee update(UUID id, Employee updated) {
    Employee existing = getById(id);
    existing.setFirstName(updated.getFirstName());
    existing.setLastName(updated.getLastName());
    existing.setEmail(updated.getEmail());
    existing.setPhone(updated.getPhone());
    existing.setDepartment(updated.getDepartment());
    return employeeRepository.save(existing);
  }

  @Override
  public void delete(UUID id) {
    // Soft delete not implemented for employee here: mark active false if BaseEntity has active
    Employee emp = getById(id);
    employeeRepository.delete(emp);
  }

  @Override
  @Transactional(readOnly = true)
  public Optional<Employee> findByEmail(String email) {
    return employeeRepository.findByEmail(email);
  }
}
