package org.godigit.trackwise.service.impl;

import lombok.RequiredArgsConstructor;
import org.godigit.trackwise.dto.AuthRequest;
import org.godigit.trackwise.dto.AuthResponse;
import org.godigit.trackwise.dto.RegistrationRequest;
import org.godigit.trackwise.dto.UserResponse;
import org.godigit.trackwise.mapper.UserMapper; // Import the mapper
import org.godigit.trackwise.model.Employee;
import org.godigit.trackwise.model.User;
import org.godigit.trackwise.model.Enum.UserStatus;
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

@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UserRepository userRepository;
    private final EmployeeRepository employeeRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final UserDetailsService userDetailsService;
    private final JwtUtil jwtUtil;

    @Override
    public AuthResponse login(AuthRequest request) {
        authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getUsername(), request.getPassword())
        );

        final UserDetails userDetails = userDetailsService.loadUserByUsername(request.getUsername());
        final String jwt = jwtUtil.generateToken(userDetails);
        return new AuthResponse(jwt);
    }

    @Override
    public UserResponse register(RegistrationRequest request) {
        if (userRepository.findByUsername(request.getUsername()).isPresent()) {
            throw new IllegalStateException("Username already exists");
        }
        if (employeeRepository.findByEmail(request.getEmail()).isPresent()) {
            throw new IllegalStateException("Email already registered");
        }

        Employee employee = new Employee();
        employee.setFirstName(request.getFirstName());
        employee.setLastName(request.getLastName());
        employee.setEmail(request.getEmail());
        employee.setPhone(request.getPhone());

        User newUser = new User();
        newUser.setUsername(request.getUsername());
        newUser.setPassword(passwordEncoder.encode(request.getPassword()));
        newUser.setRole("ROLE_USER");
        newUser.setStatus(UserStatus.PENDING_APPROVAL);
        newUser.setEmployee(employee);

        User savedUser = userRepository.save(newUser);

        // Convert the saved User entity to a DTO before returning
        return UserMapper.toResponseDTO(savedUser);
    }
}