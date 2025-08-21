package org.godigit.trackwise.mapper;


import org.godigit.trackwise.dto.UserResponse;
import org.godigit.trackwise.model.User;

public class UserMapper {
    public static UserResponse toResponseDTO(User user) {
        UserResponse dto = new UserResponse();
        dto.setUserId(user.getId());
        dto.setUsername(user.getUsername());
        dto.setRole(user.getRole());
        dto.setStatus(user.getStatus().toString());

        if (user.getEmployee() != null) {
            dto.setEmployeeId(user.getEmployee().getId());
            dto.setFullName(user.getEmployee().getFirstName() + " " + user.getEmployee().getLastName());
            dto.setEmail(user.getEmployee().getEmail());
        }
        return dto;
    }
}