package org.godigit.trackwise.controller;

import lombok.RequiredArgsConstructor;
import org.godigit.trackwise.dto.AuthRequest;
import org.godigit.trackwise.dto.AuthResponse;
import org.godigit.trackwise.dto.RegistrationRequest;
import org.godigit.trackwise.dto.UserResponse;
import org.godigit.trackwise.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@RequestBody AuthRequest request) {
        AuthResponse response = authService.login(request);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/register")
    public ResponseEntity<UserResponse> registerUser(@RequestBody RegistrationRequest request) {
        UserResponse newUserDto = authService.register(request);
        return new ResponseEntity<>(newUserDto, HttpStatus.CREATED);
    }
}