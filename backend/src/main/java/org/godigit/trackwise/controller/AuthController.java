package org.godigit.trackwise.controller;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.godigit.trackwise.dto.AuthRequest;
import org.godigit.trackwise.dto.AuthResponse;
import org.godigit.trackwise.dto.RegistrationRequest;
import org.godigit.trackwise.dto.UserResponse;
import org.godigit.trackwise.service.AuthService;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for handling user authentication and registration.
 * All endpoints under this controller are publicly accessible.
 */
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor // Lombok: Creates a constructor with all final fields.
@Tag(name = "0. Authentication", description = "Endpoints for user registration and login.")
public class AuthController {

    // The service layer that contains all the business logic for authentication.
    private final AuthService authService;

    /**
     * Authenticates a user and returns a JWT token upon successful login.
     * @param request The DTO containing the user's username and password.
     * @return A DTO containing the JWT token.
     */
    @PostMapping("/login")
    @Operation(summary = "Login a user")
    public ResponseEntity<?> login(@RequestBody AuthRequest request) {
        try {
            // Delegate the login logic to the auth service.
            AuthResponse response = authService.login(request);
            return ResponseEntity.ok(response);
        } catch (BadCredentialsException e) {
            // If authentication fails, return a 401 Unauthorized status.
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("Invalid username or password");
        }
    }

    /**
     * Registers a new user and their associated employee profile.
     * @param request The DTO containing the new user's registration data.
     * @return The newly created user's data as a DTO.
     */
    @PostMapping("/register")
    @Operation(summary = "Register a new user")
    public ResponseEntity<?> registerUser(@Valid @RequestBody RegistrationRequest request) {
        try {
            // Delegate the registration logic to the auth service.
            UserResponse newUserDto = authService.register(request);
            // Return a 201 CREATED status with the new user's data.
            return new ResponseEntity<>(newUserDto, HttpStatus.CREATED);
        } catch (IllegalStateException e) {
            // If the username or email already exists, return a 400 Bad Request.
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(e.getMessage());
        }
    }
}