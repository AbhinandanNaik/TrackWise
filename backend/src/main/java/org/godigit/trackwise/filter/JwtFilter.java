package org.godigit.trackwise.filter;

import io.jsonwebtoken.Claims;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.godigit.trackwise.util.JwtUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;

/**
 * A custom Spring Security filter that intercepts every incoming request once.
 * Its primary responsibility is to check for a JWT in the 'Authorization' header,
 * validate it, and set the user's authentication context if the token is valid.
 */
@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private JwtUtil jwtUtil;

    // This makes the code more modular and easier to test (Dependency Inversion Principle).
    @Autowired
    private UserDetailsService userDetailsService;

    /**
     * This method is executed for every incoming HTTP request.
     */
    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws IOException, ServletException {

        // 1. Get the 'Authorization' header from the request.
        final String authHeader = request.getHeader("Authorization");
        String username = null;
        String jwt = null;

        // 2. Check if the header exists and starts with "Bearer ".
        if (authHeader != null && authHeader.startsWith("Bearer ")) {
            // Extract the token (the part after "Bearer ").
            jwt = authHeader.substring(7);
            // Extract the username from the token's claims.
            username = jwtUtil.extractUsername(jwt);
        }

        // 3. If we have a username and the user is not already authenticated...
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            // ...load the user's details from the database.
            UserDetails userDetails = userDetailsService.loadUserByUsername(username);

            // 4. Validate the token against the user details.
            if (jwtUtil.validateToken(jwt, userDetails)) {
                // Extract the user's roles (authorities) directly from the token.
                List<SimpleGrantedAuthority> authorities = jwtUtil.extractAuthorities(jwt);

                // For debugging: Print the authenticated user and their roles.
                System.out.println("User '" + username + "' authenticated with authorities: " + authorities);

                // 5. Create an authentication token with the user's details and authorities.
                UsernamePasswordAuthenticationToken authToken =
                        new UsernamePasswordAuthenticationToken(userDetails, null, authorities);

                // Set details on the token (like IP address, session ID).
                authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                // 6. Set the authentication token in the SecurityContext.
                // This is the step that officially authenticates the user for this request.
                SecurityContextHolder.getContext().setAuthentication(authToken);
            }
        }

        // 7. Continue the filter chain to the next filter or the controller.
        filterChain.doFilter(request, response);
    }
}