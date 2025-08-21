package org.godigit.trackwise.dto;

import lombok.Data;
import java.util.UUID;

@Data
public class UserResponse {
    private Long userId;
    private UUID employeeId;
    private String username;
    private String fullName;
    private String email;
    private String role;
    private String status;
}