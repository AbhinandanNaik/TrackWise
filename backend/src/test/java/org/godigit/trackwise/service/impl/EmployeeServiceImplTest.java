package org.godigit.trackwise.service.impl;

import org.godigit.trackwise.dto.EmployeeRequest;
import org.godigit.trackwise.exception.NotFoundException;
import org.godigit.trackwise.model.Department;
import org.godigit.trackwise.model.Employee;
import org.godigit.trackwise.repository.DepartmentRepository;
import org.godigit.trackwise.repository.EmployeeRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.*;

class EmployeeServiceImplTest {

    @Mock
    private EmployeeRepository employeeRepository;

    @Mock
    private DepartmentRepository departmentRepository;

    @InjectMocks
    private EmployeeServiceImpl employeeService;

    private UUID employeeId;
    private UUID departmentId;
    private Employee employee;
    private Department department;
    private EmployeeRequest employeeRequest;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);

        employeeId = UUID.randomUUID();
        departmentId = UUID.randomUUID();

        department = new Department();
        department.setId(departmentId);
        department.setName("IT Department");

        employee = new Employee();
        employee.setId(employeeId);
        employee.setFirstName("John");
        employee.setLastName("Doe");
        employee.setEmail("john.doe@example.com");
        employee.setDepartment(department);

        employeeRequest = new EmployeeRequest();
        employeeRequest.setFirstName("John");
        employeeRequest.setLastName("Doe");
        employeeRequest.setEmail("john.doe@example.com");
        employeeRequest.setDepartmentId(departmentId);
    }

    @Test
    void shouldCreateEmployee() {
        // Arrange
        when(departmentRepository.findById(departmentId)).thenReturn(Optional.of(department));
        when(employeeRepository.save(any(Employee.class))).thenReturn(employee);

        // Act
        Employee result = employeeService.create(employeeRequest);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getFirstName()).isEqualTo("John");
        assertThat(result.getLastName()).isEqualTo("Doe");
        assertThat(result.getDepartment()).isEqualTo(department);

        verify(departmentRepository).findById(departmentId);
        verify(employeeRepository).save(any(Employee.class));
    }

    @Test
    void shouldThrowExceptionWhenDepartmentNotFound() {
        // Arrange
        when(departmentRepository.findById(departmentId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> employeeService.create(employeeRequest))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Department not found");

        verify(departmentRepository).findById(departmentId);
        verify(employeeRepository, never()).save(any());
    }

    @Test
    void shouldGetEmployeeById() {
        // Arrange
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));

        // Act
        Employee result = employeeService.getById(employeeId);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getId()).isEqualTo(employeeId);

        verify(employeeRepository).findById(employeeId);
    }

    @Test
    void shouldThrowExceptionWhenEmployeeNotFound() {
        // Arrange
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.empty());

        // Act & Assert
        assertThatThrownBy(() -> employeeService.getById(employeeId))
                .isInstanceOf(NotFoundException.class)
                .hasMessageContaining("Employee not found");

        verify(employeeRepository).findById(employeeId);
    }

    @Test
    void shouldListEmployees() {
        // Arrange
        Pageable pageable = PageRequest.of(0, 10);
        Page<Employee> page = new PageImpl<>(List.of(employee));
        when(employeeRepository.findAll(pageable)).thenReturn(page);

        // Act
        Page<Employee> result = employeeService.list(pageable);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getContent()).hasSize(1);
        assertThat(result.getContent().get(0).getId()).isEqualTo(employeeId);

        verify(employeeRepository).findAll(pageable);
    }

    @Test
    void shouldUpdateEmployee() {
        // Arrange
        EmployeeRequest updateDTO = new EmployeeRequest();
        updateDTO.setFirstName("Jane");
        updateDTO.setLastName("Smith");
        updateDTO.setEmail("jane.smith@example.com");
        updateDTO.setDepartmentId(departmentId);

        Employee updatedEmployee = new Employee();
        updatedEmployee.setId(employeeId);
        updatedEmployee.setFirstName("Jane");
        updatedEmployee.setLastName("Smith");
        updatedEmployee.setEmail("jane.smith@example.com");
        updatedEmployee.setDepartment(department);

        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));
        when(departmentRepository.findById(departmentId)).thenReturn(Optional.of(department));
        when(employeeRepository.save(any(Employee.class))).thenReturn(updatedEmployee);

        // Act
        Employee result = employeeService.update(employeeId, updateDTO);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result.getFirstName()).isEqualTo("Jane");
        assertThat(result.getLastName()).isEqualTo("Smith");

        verify(employeeRepository).findById(employeeId);
        verify(departmentRepository).findById(departmentId);
        verify(employeeRepository).save(any(Employee.class));
    }

    @Test
    void shouldDeleteEmployee() {
        // Arrange
        when(employeeRepository.findById(employeeId)).thenReturn(Optional.of(employee));
        doNothing().when(employeeRepository).delete(employee);

        // Act
        employeeService.delete(employeeId);

        // Assert
        verify(employeeRepository).findById(employeeId);
        verify(employeeRepository).delete(employee);
    }

    @Test
    void shouldFindEmployeeByEmail() {
        // Arrange
        String email = "john.doe@example.com";
        when(employeeRepository.findByEmail(email)).thenReturn(Optional.of(employee));

        // Act
        Optional<Employee> result = employeeService.findByEmail(email);

        // Assert
        assertThat(result).isPresent();
        assertThat(result.get().getEmail()).isEqualTo(email);

        verify(employeeRepository).findByEmail(email);
    }
}