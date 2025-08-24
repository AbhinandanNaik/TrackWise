package org.godigit.trackwise.config;

import org.springframework.context.annotation.Configuration;
import org.springframework.web.servlet.config.annotation.CorsRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Global configuration class for Spring Web MVC.
 * Its single responsibility is to define web-related beans and configurations,
 * such as CORS mappings.
 */
@Configuration // Marks this class as a source of bean definitions.
public class WebConfig implements WebMvcConfigurer {

    /**
     * Configures the Cross-Origin Resource Sharing (CORS) settings for the entire application.
     * This is necessary to allow a frontend application running on a different domain
     * to make API calls to this backend.
     *
     * @param registry The CORS registry to add mappings to.
     */
    @Override
    public void addCorsMappings(CorsRegistry registry) {
        // Create a new CORS mapping rule.
        registry.addMapping("/**") // This rule applies to all API paths in the application.

                // Allow requests from any origin.
                // In a production environment, this should be restricted to the specific
                // domain of your frontend application for better security.
                .allowedOrigins("*")

                // Specify which HTTP methods are allowed (e.g., GET, POST, etc.).
                .allowedMethods("GET", "POST", "PUT", "DELETE", "OPTIONS")

                // Allow all headers to be sent in the request (e.g., Authorization for JWT).
                .allowedHeaders("*");
    }
}