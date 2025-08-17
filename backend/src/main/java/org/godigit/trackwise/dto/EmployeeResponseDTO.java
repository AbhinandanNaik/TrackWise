package org.godigit.trackwise.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class EmployeeResponseDTO {
    private UUID id;
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private UUID departmentId;
    private String departmentName; // extra info if you want
}
