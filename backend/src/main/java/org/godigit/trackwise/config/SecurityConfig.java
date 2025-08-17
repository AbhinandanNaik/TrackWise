package org.godigit.trackwise.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http
                // THIS IS THE FIX: Disable CSRF protection for your stateless API
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth
                        // This rule allows access to your API without logging in
                        .requestMatchers("/api/**").permitAll()
                        // Any other request (if you add them later) will still be secured
                        .anyRequest().authenticated()
                );

        return http.build();
    }
}