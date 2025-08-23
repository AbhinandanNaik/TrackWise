package org.godigit.trackwise.config;

import org.godigit.trackwise.job.AssetPerformanceAnalysisJob;
import org.godigit.trackwise.job.NewsScannerJob;
import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * Configuration class for setting up all scheduled background jobs using Quartz.
 * This class defines the jobs (what to run) and their triggers (when to run them).
 */
@Configuration
public class QuartzConfig {

    // --- News Scanner Job Configuration ---

    /**
     * Defines the details of the News Scanner job.
     * A JobDetail is a durable definition of a job that is independent of any specific schedule.
     * @return A JobDetail instance for the NewsScannerJob.
     */
    @Bean
    public JobDetail newsScannerJobDetail() {
        return JobBuilder.newJob(NewsScannerJob.class) // Specifies the class that contains the job logic.
                .withIdentity("newsScannerJob") // Gives the job a unique name.
                .storeDurably() // Allows the job to exist even without a trigger.
                .build();
    }

    /**
     * Defines the trigger (the schedule) for the News Scanner job.
     * This trigger uses a cron expression to run the job at a specific time.
     * @param newsScannerJobDetail The JobDetail bean to associate this trigger with.
     * @return A Trigger instance that will execute the news scanner job.
     */
    @Bean
    public Trigger newsScannerJobTrigger(JobDetail newsScannerJobDetail) {
        // Cron Expression: "0 0 2 * * ?" means run daily at 2:00 AM.
        // For testing, you can use "0 * * ? * *" to run it every minute.
        return TriggerBuilder.newTrigger().forJob(newsScannerJobDetail) // Links this trigger to the specific job.
                .withIdentity("newsScannerTrigger") // Gives the trigger a unique name.
                .withSchedule(CronScheduleBuilder.cronSchedule("0 0 2 * * ?")) // Sets the schedule.
                .build();
    }

    // --- Asset Performance Analysis Job Configuration ---

    /**
     * Defines the details of the Asset Performance Analysis job.
     * @return A JobDetail instance for the AssetPerformanceAnalysisJob.
     */
    @Bean
    public JobDetail assetPerformanceJobDetail() {
        return JobBuilder.newJob(AssetPerformanceAnalysisJob.class)
                .withIdentity("assetPerformanceJob")
                .storeDurably()
                .build();
    }

    /**
     * Defines the trigger for the Asset Performance Analysis job.
     * @param assetPerformanceJobDetail The JobDetail bean to associate this trigger with.
     * @return A Trigger instance that will execute the performance analysis job.
     */
    @Bean
    public Trigger assetPerformanceJobTrigger(JobDetail assetPerformanceJobDetail) {
        // Cron Expression: "0 0 3 ? * SUN" means run every Sunday at 3:00 AM.
        return TriggerBuilder.newTrigger().forJob(assetPerformanceJobDetail)
                .withIdentity("assetPerformanceTrigger")
                .withSchedule(CronScheduleBuilder.cronSchedule("0 0 3 ? * SUN"))
                .build();
    }
}