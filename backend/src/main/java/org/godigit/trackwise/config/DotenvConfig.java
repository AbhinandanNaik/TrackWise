package org.godigit.trackwise.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertiesPropertySource;

import java.util.Properties;

/**
 * A custom EnvironmentPostProcessor that loads variables from a .env file
 * into the Spring Environment. This ensures that secrets and environment-specific
 * configurations are available to the application at the very beginning of the
 * startup process, even before standard application.properties are fully processed.
 */
public class DotenvConfig implements EnvironmentPostProcessor {

    /**
     * This method is called by Spring Boot very early in the application startup sequence.
     * @param environment The configurable environment for the application.
     * @param application The current SpringApplication.
     */
    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        // Configure the Dotenv loader.
        Dotenv dotenv = Dotenv.configure()
                // Explicitly point to the .env file in the 'backend' directory,
                // which is necessary in a multi-module project run from the root.
                .directory("./backend")
                // Do not throw an error if the .env file is not found (e.g., in production).
                .ignoreIfMissing()
                // Load the file.
                .load();

        // Create a new Properties object to hold the variables from the .env file.
        Properties props = new Properties();

        // Iterate through all entries in the .env file and add them to our Properties object.
        dotenv.entries().forEach(entry -> props.setProperty(entry.getKey(), entry.getValue()));

        // Add the loaded properties to the Spring Environment as a new PropertySource.
        // By using 'addFirst', we ensure that these properties have high precedence
        // and can be used to resolve placeholders in application.properties.
        environment.getPropertySources().addFirst(new PropertiesPropertySource("dotenvProperties", props));
    }
}