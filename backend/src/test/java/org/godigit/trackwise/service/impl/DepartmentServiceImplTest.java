package org.godigit.trackwise.service.impl;

import org.godigit.trackwise.dto.DepartmentRequest;
import org.godigit.trackwise.dto.DepartmentResponse;
import org.godigit.trackwise.exception.NotFoundException;
import org.godigit.trackwise.model.Department;
import org.godigit.trackwise.repository.DepartmentRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class DepartmentServiceImplTest {

    @Mock
    private DepartmentRepository departmentRepository;

    @InjectMocks
    private DepartmentServiceImpl departmentService;

    private UUID deptId;
    private Department department;
    private DepartmentRequest request;

    @BeforeEach
    void setUp() {
        deptId = UUID.randomUUID();

        department = new Department();
        department.setId(deptId);
        department.setName("Engineering");
        department.setLocation("Building A");

        request = new DepartmentRequest();
        request.setName("Engineering");
        request.setLocation("Building A");
    }

    @Test
    void shouldCreateDepartment() {
        Department saved = new Department();
        saved.setId(deptId);
        saved.setName(request.getName());
        saved.setLocation(request.getLocation());

        when(departmentRepository.save(any(Department.class))).thenReturn(saved);

        DepartmentResponse resp = departmentService.create(request);

        assertThat(resp).isNotNull();
        assertThat(resp.getId()).isEqualTo(deptId);
        assertThat(resp.getName()).isEqualTo(request.getName());
        assertThat(resp.getLocation()).isEqualTo(request.getLocation());

        verify(departmentRepository).save(any(Department.class));
    }

    @Test
    void shouldGetById() {
        when(departmentRepository.findById(deptId)).thenReturn(Optional.of(department));

        DepartmentResponse resp = departmentService.getById(deptId);

        assertThat(resp).isNotNull();
        assertThat(resp.getId()).isEqualTo(deptId);
        assertThat(resp.getName()).isEqualTo(department.getName());

        verify(departmentRepository).findById(deptId);
    }

    @Test
    void getById_shouldThrowWhenNotFound() {
        when(departmentRepository.findById(deptId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> departmentService.getById(deptId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Department not found");

        verify(departmentRepository).findById(deptId);
    }

    @Test
    void shouldListDepartments() {
        Pageable pageable = PageRequest.of(0, 10);
        Page<Department> page = new PageImpl<>(List.of(department));

        when(departmentRepository.findAll(pageable)).thenReturn(page);

        Page<DepartmentResponse> result = departmentService.list(pageable);

        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getId()).isEqualTo(deptId);

        verify(departmentRepository).findAll(pageable);
    }

    @Test
    void shouldUpdateDepartment() {
        DepartmentRequest updateReq = new DepartmentRequest();
        updateReq.setName("R&D");
        updateReq.setLocation("Building B");

        Department existing = new Department();
        existing.setId(deptId);
        existing.setName("OldName");
        existing.setLocation("OldLoc");

        Department updated = new Department();
        updated.setId(deptId);
        updated.setName(updateReq.getName());
        updated.setLocation(updateReq.getLocation());

        when(departmentRepository.findById(deptId)).thenReturn(Optional.of(existing));
        when(departmentRepository.save(existing)).thenReturn(updated);

        DepartmentResponse resp = departmentService.update(deptId, updateReq);

        assertThat(resp).isNotNull();
        assertThat(resp.getId()).isEqualTo(deptId);
        assertThat(resp.getName()).isEqualTo("R&D");
        assertThat(resp.getLocation()).isEqualTo("Building B");

        verify(departmentRepository).findById(deptId);
        verify(departmentRepository).save(existing);
    }

    @Test
    void update_shouldThrowWhenNotFound() {
        DepartmentRequest updateReq = new DepartmentRequest();
        updateReq.setName("R&D");
        updateReq.setLocation("Building B");

        when(departmentRepository.findById(deptId)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> departmentService.update(deptId, updateReq))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Department not found");

        verify(departmentRepository).findById(deptId);
    }

    @Test
    void shouldDeleteDepartment() {
        when(departmentRepository.existsById(deptId)).thenReturn(true);
        doNothing().when(departmentRepository).deleteById(deptId);

        assertThatCode(() -> departmentService.delete(deptId)).doesNotThrowAnyException();

        verify(departmentRepository).existsById(deptId);
        verify(departmentRepository).deleteById(deptId);
    }

    @Test
    void delete_shouldThrowWhenNotFound() {
        when(departmentRepository.existsById(deptId)).thenReturn(false);

        assertThatThrownBy(() -> departmentService.delete(deptId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Department not found");

        verify(departmentRepository).existsById(deptId);
        verify(departmentRepository, never()).deleteById(any());
    }
}