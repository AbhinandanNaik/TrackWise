package org.godigit.trackwise.config;

import io.github.cdimascio.dotenv.Dotenv;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.env.EnvironmentPostProcessor;
import org.springframework.core.env.ConfigurableEnvironment;
import org.springframework.core.env.PropertiesPropertySource;

import java.util.Properties;

public class DotenvConfig implements EnvironmentPostProcessor {

    @Override
    public void postProcessEnvironment(ConfigurableEnvironment environment, SpringApplication application) {
        // Explicitly point to the .env file in the backend directory
        Dotenv dotenv = Dotenv.configure()
                .directory("./backend")
                .ignoreIfMissing()
                .load();

        Properties props = new Properties();
        dotenv.entries().forEach(entry -> props.setProperty(entry.getKey(), entry.getValue()));

        // Add the .env properties to the Spring Environment with high precedence
        environment.getPropertySources().addFirst(new PropertiesPropertySource("dotenvProperties", props));
    }
}