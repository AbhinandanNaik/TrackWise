package org.godigit.trackwise.service.impl;

import org.godigit.trackwise.dto.AuthRequest;
import org.godigit.trackwise.dto.AuthResponse;
import org.godigit.trackwise.dto.RegistrationRequest;
import org.godigit.trackwise.dto.UserResponse;
import org.godigit.trackwise.model.Employee;
import org.godigit.trackwise.model.User;
import org.godigit.trackwise.model.Enum.UserStatus;
import org.godigit.trackwise.repository.EmployeeRepository;
import org.godigit.trackwise.repository.UserRepository;
import org.godigit.trackwise.util.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.*;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

class AuthServiceImplTest {

    @Mock
    private UserRepository userRepository;
    @Mock
    private EmployeeRepository employeeRepository;
    @Mock
    private PasswordEncoder passwordEncoder;
    @Mock
    private AuthenticationManager authenticationManager;
    @Mock
    private UserDetailsService userDetailsService;
    @Mock
    private JwtUtil jwtUtil;

    @InjectMocks
    private AuthServiceImpl authService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    // ---------- LOGIN TESTS ----------

    @Test
    void login_ShouldReturnJwtToken_WhenCredentialsAreValid() {
        AuthRequest request = new AuthRequest("john", "password");

        UserDetails mockUserDetails = mock(UserDetails.class);

        when(userDetailsService.loadUserByUsername("john")).thenReturn(mockUserDetails);
        when(jwtUtil.generateToken(mockUserDetails)).thenReturn("mock-jwt");

        AuthResponse response = authService.login(request);

        verify(authenticationManager).authenticate(
                new UsernamePasswordAuthenticationToken("john", "password")
        );
        verify(userDetailsService).loadUserByUsername("john");
        verify(jwtUtil).generateToken(mockUserDetails);

        assertEquals("mock-jwt", response.getJwt());
    }

    // ---------- REGISTER TESTS ----------

    @Test
    void register_ShouldSaveUser_WhenValidRequest() {
        RegistrationRequest request = new RegistrationRequest();
        request.setUsername("john");
        request.setPassword("plainpass");
        request.setFirstName("John");
        request.setLastName("Doe");
        request.setEmail("john@example.com");
        request.setPhone("1234567890");

        when(userRepository.findByUsername("john")).thenReturn(Optional.empty());
        when(employeeRepository.findByEmail("john@example.com")).thenReturn(Optional.empty());
        when(passwordEncoder.encode("plainpass")).thenReturn("hashedpass");

        User savedUser = new User();
        savedUser.setUsername("john");
        savedUser.setPassword("hashedpass");
        savedUser.setRole("ROLE_USER");
        savedUser.setStatus(UserStatus.PENDING_APPROVAL);
        Employee emp = new Employee();
        emp.setEmail("john@example.com");
        savedUser.setEmployee(emp);

        when(userRepository.save(any(User.class))).thenReturn(savedUser);

        UserResponse response = authService.register(request);

        verify(userRepository).save(any(User.class));
        assertEquals("john", response.getUsername());
        assertEquals("ROLE_USER", response.getRole());
    }

    @Test
    void register_ShouldThrowException_WhenUsernameAlreadyExists() {
        RegistrationRequest request = new RegistrationRequest();
        request.setUsername("john");
        request.setEmail("john@example.com");

        when(userRepository.findByUsername("john")).thenReturn(Optional.of(new User()));

        assertThrows(IllegalStateException.class, () -> authService.register(request));
    }

    @Test
    void register_ShouldThrowException_WhenEmailAlreadyExists() {
        RegistrationRequest request = new RegistrationRequest();
        request.setUsername("john");
        request.setEmail("john@example.com");

        when(userRepository.findByUsername("john")).thenReturn(Optional.empty());
        when(employeeRepository.findByEmail("john@example.com")).thenReturn(Optional.of(new Employee()));

        assertThrows(IllegalStateException.class, () -> authService.register(request));
    }
}
