package org.godigit.trackwise.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class EmployeeRequestDTO {
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private UUID departmentId; // just the id, service will resolve the Department
}
