package org.godigit.trackwise.mapper;

import org.godigit.trackwise.dto.EmployeeRequest;
import org.godigit.trackwise.dto.EmployeeResponse;
import org.godigit.trackwise.model.Department;
import org.godigit.trackwise.model.Employee;

public class EmployeeMapper {

    // Map from Request DTO + Department entity → Employee
    public static Employee toEntity(EmployeeRequest dto, Department department) {
        Employee employee = new Employee();
        employee.setFirstName(dto.getFirstName());
        employee.setLastName(dto.getLastName());
        employee.setEmail(dto.getEmail());
        employee.setPhone(dto.getPhone());
        employee.setDepartment(department);
        return employee;
    }

    // Map Employee → Response DTO
    public static EmployeeResponse toDto(Employee employee) {
        EmployeeResponse dto = new EmployeeResponse();
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
