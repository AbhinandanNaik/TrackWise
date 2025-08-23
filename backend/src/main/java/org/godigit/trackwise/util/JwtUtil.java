package org.godigit.trackwise.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * A utility class for handling all JWT (JSON Web Token) operations.
 * This includes generating tokens, extracting claims, and validating tokens.
 * It is a core component of the application's security.
 */
@Component
public class JwtUtil {

    // Injects the JWT secret key from the application.properties or .env file.
    @Value("${jwt.secret}")
    private String secret;

    // The secure key used for signing and verifying tokens.
    private Key key;

    /**
     * This method runs after the bean is constructed. It converts the plain-text
     * secret into a secure, cryptographic key suitable for the HS256 algorithm.
     */
    @PostConstruct
    public void init() {
        this.key = Keys.hmacShaKeyFor(secret.getBytes());
    }

    /**
     * Generates a new JWT for a given authenticated user.
     * @param userDetails The user details provided by Spring Security.
     * @return A signed, compact JWT string.
     */
    public String generateToken(UserDetails userDetails) {
        // Create a map to hold custom claims.
        Map<String, Object> claims = new HashMap<>();
        // Add the user's roles (authorities) as a custom claim.
        claims.put("roles", userDetails.getAuthorities());

        // Build the JWT.
        return Jwts.builder()
                .setClaims(claims) // Set the custom claims.
                .setSubject(userDetails.getUsername()) // Set the subject to the username.
                .setIssuedAt(new Date()) // Set the token's creation time to now.
                .setExpiration(new Date(System.currentTimeMillis() + 1000 * 60 * 60 * 10)) // Set expiration to 10 hours from now.
                .signWith(key, SignatureAlgorithm.HS256) // Sign the token with the secure key.
                .compact(); // Build the final, compact URL-safe string.
    }

    /**
     * Extracts the username (subject) from a JWT.
     * @param token The JWT string.
     * @return The username.
     */
    public String extractUsername(String token) {
        return extractAllClaims(token).getSubject();
    }

    /**
     * Validates a JWT. It checks if the username in the token matches the
     * UserDetails and if the token has not expired.
     * @param token The JWT string.
     * @param userDetails The user details to validate against.
     * @return True if the token is valid, false otherwise.
     */
    public boolean validateToken(String token, UserDetails userDetails) {
        final String username = extractUsername(token);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
    }

    /**
     * Checks if a JWT has expired.
     * @param token The JWT string.
     * @return True if the token's expiration date is before the current time.
     */
    private boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }

    /**
     * Extracts the list of authorities (roles) from the "roles" claim in a JWT.
     * @param token The JWT string.
     * @return A list of SimpleGrantedAuthority objects.
     */
    public List<SimpleGrantedAuthority> extractAuthorities(String token) {
        Claims claims = extractAllClaims(token);
        // The JWT library decodes the roles claim into a List of Maps.
        List<Map<String, String>> roles = claims.get("roles", List.class);
        // We need to map this list to a list of SimpleGrantedAuthority objects.
        return roles.stream()
                .map(roleMap -> new SimpleGrantedAuthority(roleMap.get("authority")))
                .collect(Collectors.toList());
    }

    /**
     * A helper method to parse a JWT and extract all its claims.
     * @param token The JWT string.
     * @return The Claims object containing all data from the token.
     */
    public Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(key) // Use the same key to verify the signature.
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
}