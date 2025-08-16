package org.godigit.trackwise.mapper;

import org.godigit.trackwise.model.Employee;
import org.godigit.trackwise.dto.EmployeeResponseDTO;
import org.godigit.trackwise.dto.EmployeeRequestDTO;
import org.godigit.trackwise.model.Department;

public class EmployeeMapper {

    public static EmployeeResponseDTO toDTO(Employee emp) {
        EmployeeResponseDTO dto = new EmployeeResponseDTO();
        dto.setId(emp.getId());
        dto.setFirstName(emp.getFirstName());
        dto.setLastName(emp.getLastName());
        dto.setEmail(emp.getEmail());
        dto.setPhone(emp.getPhone());
        dto.setDepartmentName(emp.getDepartment() != null ? emp.getDepartment().getName() : null);
        return dto;
    }

    public static Employee toEntity(EmployeeRequestDTO dto, Department dept) {
        Employee emp = new Employee();
        emp.setFirstName(dto.getFirstName());
        emp.setLastName(dto.getLastName());
        emp.setEmail(dto.getEmail());
        emp.setPhone(dto.getPhone());
        emp.setDepartment(dept);
        return emp;
    }
}
