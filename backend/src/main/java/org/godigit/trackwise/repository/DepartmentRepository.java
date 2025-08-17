package org.godigit.trackwise.repository;

import org.godigit.trackwise.model.Department;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.UUID;

@Repository
public interface DepartmentRepository extends JpaRepository<Department, UUID> {
    // You can add custom queries if needed, for now basic CRUD is enough
}
