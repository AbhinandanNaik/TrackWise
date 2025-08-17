package org.godigit.trackwise.repository;

import org.godigit.trackwise.model.Employee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;
import java.util.UUID;

public interface EmployeeRepository extends JpaRepository<Employee, UUID> {

  Optional<Employee> findByEmail(String email);

}
