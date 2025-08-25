package org.godigit.trackwise;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

public class PasswordGenerator {
    public static void main(String[] args) {
        PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

        // The plain-text password you want to hash
        String rawPassword = "rajesh123";

        // Generate the hash
        String hashedPassword = passwordEncoder.encode(rawPassword);

        // Print the result
        System.out.println("Raw Password: " + rawPassword);
        System.out.println("BCrypt Hash: " + hashedPassword);
    }
}