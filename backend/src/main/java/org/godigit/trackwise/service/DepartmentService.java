package org.godigit.trackwise.service;

import org.godigit.trackwise.dto.DepartmentRequest;
import org.godigit.trackwise.dto.DepartmentResponse;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.UUID;

/**
 * Service interface defining the business operations for Department management.
 */
public interface DepartmentService {
    DepartmentResponse create(DepartmentRequest request);
    DepartmentResponse getById(UUID id);
    Page<DepartmentResponse> list(Pageable pageable);
    DepartmentResponse update(UUID id, DepartmentRequest request);
    void delete(UUID id);
}