package org.godigit.trackwise.config;

import org.godigit.trackwise.filter.JwtFilter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

/**
 * Main configuration class for Spring Security.
 * This class defines all security rules, password encoding, and JWT filter integration.
 */
@Configuration
@EnableWebSecurity // Enables Spring Security's web security support.
public class SecurityConfig {

    // Injects the custom JWT filter that processes the token on each request.
    @Autowired
    private JwtFilter jwtFilter;

    // A centralized array of all URLs that should be publicly accessible without authentication.
    private static final String[] PUBLIC_URLS = {
            "/api/auth/**",       // For login and registration
            "/v3/api-docs/**",    // For OpenAPI 3 specification
            "/swagger-ui/**",     // For the Swagger UI interface
            "/swagger-ui.html",
            "/actuator/**"
    };

    /**
     * Defines the main security filter chain that applies to all HTTP requests.
     * @param http The HttpSecurity object to configure.
     * @return The configured SecurityFilterChain.
     * @throws Exception
     */
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // Disable CSRF (Cross-Site Request Forgery) protection.
                // This is standard practice for stateless REST APIs that use tokens for authentication.
                .csrf(csrf -> csrf.disable())

                // Define authorization rules for all HTTP requests.
                .authorizeHttpRequests(auth -> auth
                        // 1. Permit all requests to the public URLs defined above.
                        .requestMatchers(PUBLIC_URLS).permitAll()

                        // 2. Define role-based access rules. Rules are checked in order.
                        .requestMatchers("/api/admin/**").hasRole("ADMIN") // Only users with ROLE_ADMIN can access admin endpoints.
                        .requestMatchers(HttpMethod.GET, "/api/**").hasAnyRole("USER", "ADMIN") // Any authenticated user can view data.
                        .requestMatchers(HttpMethod.POST, "/api/**").hasRole("ADMIN") // Only admins can create data.
                        .requestMatchers(HttpMethod.PUT, "/api/**").hasRole("ADMIN") // Only admins can update data.
                        .requestMatchers(HttpMethod.DELETE, "/api/**").hasRole("ADMIN") // Only admins can delete data.

                        // 3. Any other request that doesn't match the rules above must be authenticated.
                        .anyRequest().authenticated()
                )

                // Configure session management to be stateless.
                // This tells Spring Security not to create or use any server-side sessions,
                // as each request will be authenticated independently via the JWT.
                .sessionManagement(session -> session
                        .sessionCreationPolicy(SessionCreationPolicy.STATELESS)
                );

        // Add the custom JwtFilter to the security chain before the standard username/password filter.
        // This ensures our token is validated on every request to a protected endpoint.
        http.addFilterBefore(jwtFilter, UsernamePasswordAuthenticationFilter.class);

        // Build and return the configured security filter chain.
        return http.build();
    }

    /**
     * Exposes the AuthenticationManager as a Spring bean.
     * This is needed by the AuthController to process login requests.
     * @param config The authentication configuration.
     * @return The configured AuthenticationManager.
     * @throws Exception
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration config) throws Exception {
        return config.getAuthenticationManager();
    }

    /**
     * Defines the PasswordEncoder bean for the application.
     * BCrypt is the industry-standard, strong hashing algorithm for passwords.
     * @return A BCryptPasswordEncoder instance.
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }
}