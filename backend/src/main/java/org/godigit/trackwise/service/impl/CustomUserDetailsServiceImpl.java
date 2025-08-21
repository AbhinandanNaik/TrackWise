package org.godigit.trackwise.service.impl;


import org.godigit.trackwise.model.User;
import org.godigit.trackwise.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class CustomUserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    // In CustomUserDetailsService.java

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));

        // The role from the database ("ROLE_ADMIN") is already correct.
        // We don't need to add a prefix.
        List<SimpleGrantedAuthority> authorities = List.of(
                new SimpleGrantedAuthority(user.getRole())
        );

        System.out.println("Loading user: " + username + " with roles: " + authorities);

        return new org.springframework.security.core.userdetails.User(
                user.getUsername(),
                user.getPassword(),
                authorities
        );
    }
}
