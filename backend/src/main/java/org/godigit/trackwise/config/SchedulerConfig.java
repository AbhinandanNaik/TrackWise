package org.godigit.trackwise.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.concurrent.ThreadPoolTaskScheduler;

/**
 * Configuration class for creating a custom, multi-threaded task scheduler.
 * This bean will be used by Spring for running scheduled tasks, such as the IoT simulator.
 */
@Configuration // Marks this class as a source of bean definitions.
public class SchedulerConfig {

    /**
     * Defines and configures the primary TaskScheduler bean for the application.
     * This overrides the default single-threaded scheduler provided by Spring Boot.
     * @return A configured ThreadPoolTaskScheduler instance.
     */
    @Bean
    public ThreadPoolTaskScheduler taskScheduler() {
        // Create a new scheduler instance.
        ThreadPoolTaskScheduler scheduler = new ThreadPoolTaskScheduler();

        // Set the number of threads in the pool to 5.
        // This allows up to 5 scheduled tasks to run concurrently.
        scheduler.setPoolSize(5);

        // Set a custom prefix for the thread names.
        // This is very useful for debugging, as it makes it easy to identify
        // which log messages are coming from your scheduled tasks.
        scheduler.setThreadNamePrefix("iot-scheduler-");

        // Return the configured scheduler bean for Spring to manage.
        return scheduler;
    }
}