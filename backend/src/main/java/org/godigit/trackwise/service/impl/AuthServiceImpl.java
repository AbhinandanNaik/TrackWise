package org.godigit.trackwise.service.impl;

import lombok.RequiredArgsConstructor;
import org.godigit.trackwise.dto.AuthRequest;
import org.godigit.trackwise.dto.AuthResponse;
import org.godigit.trackwise.dto.RegistrationRequest;
import org.godigit.trackwise.dto.UserResponse;
import org.godigit.trackwise.mapper.UserMapper;
import org.godigit.trackwise.model.Employee;
import org.godigit.trackwise.model.User;
import org.godigit.trackwise.model.Enum.*;
import org.godigit.trackwise.repository.EmployeeRepository;
import org.godigit.trackwise.repository.UserRepository;
import org.godigit.trackwise.service.AuthService;
import org.godigit.trackwise.util.JwtUtil;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

/**
 * Service implementation for handling user authentication and registration.
 * This class contains the core business logic for the auth workflow.
 */
@Service
@RequiredArgsConstructor // Lombok: Creates a constructor with all final fields.
@Transactional // Ensures all public methods run inside a database transaction.
public class AuthServiceImpl implements AuthService {

    // Dependencies injected by the constructor.
    private final UserRepository userRepository;
    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;

    /**
     * Authenticates a user with their username and password.
     * @param request The DTO containing the login credentials.
     * @return A DTO containing the generated JWT token.
     */
    @Override
    public AuthResponse login(AuthRequest request) {
        // Use Spring Security's AuthenticationManager to validate the credentials.
        // This will automatically use the PasswordEncoder to compare the hashes.
        // If authentication fails, it throws a BadCredentialsException.
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        // If authentication is successful, load the user details again.
        final UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());

        // Generate a JWT token for the authenticated user.
        final String jwt = jwtUtil.generateToken(userDetails);

        // Return the token in a response object.
        return new AuthResponse(jwt);
    }

    /**
     * Registers a new user and their associated employee profile.
     * @param request The DTO containing the registration data.
     * @return A DTO representing the newly created user.
     */
    @Override
    public UserResponse register(RegistrationRequest request) {
        // Business Rule: Ensure the username is unique.
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new IllegalStateException("Username already exists");
        }

        // Business Rule: Ensure the employee's email is unique.
        if (employeeRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalStateException("Email already registered");
        }

        // 1. Create the Employee profile from the DTO.
        Employee employee = new Employee();
        employee.setFirstName(request.getFirstName());
        employee.setLastName(request.getLastName());
        employee.setEmail(request.getEmail());
        employee.setPhone(request.getPhone());

        // 2. Create the User login credentials.
        User newUser = new User();
        newUser.setUsername(request.getUsername());

        // Security: Hash the plain-text password before saving.
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));

        // Set default role and status for all new signups.
        newUser.setRole("ROLE_USER");
        newUser.setStatus(UserStatus.PENDING_APPROVAL);

        // 3. Link the User to the Employee profile.
        newUser.setEmployee(employee);

        // 4. Save the User. Because of CascadeType.ALL, the new Employee is also saved.
        User savedUser = userRepository.save(newUser);

        // 5. Convert the saved entity to a DTO to safely return to the client.
        return UserMapper.toResponseDTO(savedUser);
    }
}