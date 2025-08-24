package org.godigit.trackwise.service;

import org.godigit.trackwise.dto.AuthRequest;
import org.godigit.trackwise.dto.AuthResponse;
import org.godigit.trackwise.dto.RegistrationRequest;
import org.godigit.trackwise.dto.UserResponse;

public interface AuthService {

    AuthResponse login(AuthRequest request);
    UserResponse register(RegistrationRequest request);
}