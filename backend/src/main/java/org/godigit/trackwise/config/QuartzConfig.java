package org.godigit.trackwise.config;

import org.godigit.trackwise.job.NewsScannerJob;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.scheduling.quartz.CronTriggerFactoryBean;

import java.text.ParseException;

@Configuration
public class QuartzConfig {

    /**
     * This defines the details of the job itself.
     * It links to your NewsScannerJob class.
     */
    @Bean
    public JobDetail newsScannerJobDetail() {
        return JobBuilder.newJob(NewsScannerJob.class)
                .withIdentity("newsScannerJob")
                .storeDurably()
                .build();
    }

    /**
     * This defines the trigger (the schedule) for the job.
     * This example uses a Cron expression to run the job once every day at 2:00 AM.
     */
    @Bean
    public Trigger newsScannerJobTrigger(JobDetail newsScannerJobDetail) throws ParseException {
        CronTriggerFactoryBean factoryBean = new CronTriggerFactoryBean();
        factoryBean.setJobDetail(newsScannerJobDetail);
        factoryBean.setName("newsScannerTrigger");
        factoryBean.setCronExpression("0 0 2 * * ?"); // Run daily at 2 AM
        // For testing, you could run it every minute: "0 * * ? * *"
        factoryBean.afterPropertiesSet();
        return factoryBean.getObject();
    }
}