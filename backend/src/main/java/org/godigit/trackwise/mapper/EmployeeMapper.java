package org.godigit.trackwise.mapper;

import org.godigit.trackwise.dto.EmployeeRequestDTO;
import org.godigit.trackwise.dto.EmployeeResponseDTO;
import org.godigit.trackwise.model.Department;
import org.godigit.trackwise.model.Employee;

public class EmployeeMapper {

    // Map from Request DTO + Department entity → Employee
    public static Employee toEntity(EmployeeRequestDTO dto, Department department) {
        Employee employee = new Employee();
        employee.setFirstName(dto.getFirstName());
        employee.setLastName(dto.getLastName());
        employee.setEmail(dto.getEmail());
        employee.setPhone(dto.getPhone());
        employee.setDepartment(department);
        return employee;
    }

    // Map Employee → Response DTO
    public static EmployeeResponseDTO toDto(Employee employee) {
        EmployeeResponseDTO dto = new EmployeeResponseDTO();
        dto.setId(employee.getId());
        dto.setFirstName(employee.getFirstName());
        dto.setLastName(employee.getLastName());
        dto.setEmail(employee.getEmail());
        dto.setPhone(employee.getPhone());
        if (employee.getDepartment() != null) {
            dto.setDepartmentId(employee.getDepartment().getId());
            dto.setDepartmentName(employee.getDepartment().getName());
        }
        return dto;
    }
}
