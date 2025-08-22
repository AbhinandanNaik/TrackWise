package org.godigit.trackwise.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class SignupRequest {
    private String username;
    private String password;
    private String role;

    private String firstName;
    private String lastName;
    private String email;
    private String phone;
    private Long departmentId; // reference to Department
}
