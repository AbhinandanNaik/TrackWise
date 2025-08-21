package org.godigit.trackwise.dto;

import lombok.Data;

@Data
public class RegistrationRequest {
    // Employee details
    private String firstName;
    private String lastName;
    private String email;
    private String phone;

    // Login details
    private String username;
    private String password;
}