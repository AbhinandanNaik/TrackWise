package org.godigit.trackwise.dto;

import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
public class EmployeeRequestDTO {
    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private UUID departmentId; // just the ID, not the full Department object

    // getters and setters
}
