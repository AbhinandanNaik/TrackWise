package org.godigit.trackwise.service.impl;

import org.godigit.trackwise.model.User;
import org.godigit.trackwise.repository.UserRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.Optional;
import java.util.stream.Collectors;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class CustomUserDetailsServiceImplTest {

    @Mock
    private UserRepository userRepository;

    @InjectMocks
    private CustomUserDetailsServiceImpl service;

    private User user;

    @BeforeEach
    void setUp() {
        user = new User();
        user.setUsername("alice");
        user.setPassword("secret");
        // role stored in DB already includes prefix if desired; service shouldn't add/modify it
        user.setRole("ROLE_ADMIN");
    }

    @Test
    void loadUserByUsername_shouldReturnUserDetails_whenUserExists() {
        // Arrange
        when(userRepository.findByUsername("alice")).thenReturn(Optional.of(user));

        // Act
        UserDetails details = service.loadUserByUsername("alice");

        // Assert
        assertThat(details).isNotNull();
        assertThat(details.getUsername()).isEqualTo("alice");
        assertThat(details.getPassword()).isEqualTo("secret");

        // authorities preserved exactly as stored in user.role
        assertThat(details.getAuthorities())
                .extracting(GrantedAuthority::getAuthority)
                .containsExactly("ROLE_ADMIN");

        // verify repository was called
        verify(userRepository).findByUsername("alice");
    }

    @Test
    void loadUserByUsername_shouldThrow_whenUserNotFound() {
        // Arrange
        when(userRepository.findByUsername("bob")).thenReturn(Optional.empty());

        // Act / Assert
        assertThatThrownBy(() -> service.loadUserByUsername("bob"))
                .isInstanceOf(UsernameNotFoundException.class)
                .hasMessageContaining("User not found");

        verify(userRepository).findByUsername("bob");
    }

    @Test
    void loadUserByUsername_shouldUseRoleAsIs_evenIfNoRolePrefix() {
        // Arrange: role without ROLE_ prefix
        user.setUsername("charlie");
        user.setRole("ADMIN");
        when(userRepository.findByUsername("charlie")).thenReturn(Optional.of(user));

        // Act
        UserDetails details = service.loadUserByUsername("charlie");

        // Assert: authority should exactly match "ADMIN"
        assertThat(details.getAuthorities()
                .stream()
                .map(GrantedAuthority::getAuthority)
                .collect(Collectors.toList()))
                .containsExactly("ADMIN");

        verify(userRepository).findByUsername("charlie");
    }
}