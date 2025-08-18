package org.godigit.trackwise.job;

import lombok.RequiredArgsConstructor;
import org.godigit.trackwise.dto.ArticleDTO;
import org.godigit.trackwise.dto.EmailRequestDTO;
import org.godigit.trackwise.dto.NotificationRequestDTO;
import org.godigit.trackwise.model.Employee;
import org.godigit.trackwise.repository.AssetRepository;
import org.godigit.trackwise.repository.EmployeeRepository;
import org.godigit.trackwise.service.NewsService;
import org.godigit.trackwise.service.NotificationService;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;
import org.springframework.scheduling.quartz.QuartzJobBean;

import java.util.List;

@Component
@RequiredArgsConstructor
public class NewsScannerJob extends QuartzJobBean {

    private static final Logger log = LoggerFactory.getLogger(NewsScannerJob.class);
    private final AssetRepository assetRepository;
    private final EmployeeRepository employeeRepository; // To find an admin
    private final NewsService newsService;
    private final NotificationService notificationService; // Inject NotificationService

    @Override
    public void executeInternal(JobExecutionContext context) {
        log.info("Starting daily news scan for assets...");

        // Find an admin or a specific user to send alerts to.
        // For this example, we'll just find the first employee.
        Employee admin = employeeRepository.findAll().stream().findFirst().orElse(null);
        if (admin == null) {
            log.warn("News scanner running, but no employees found to send notifications to. Aborting.");
            return;
        }

        List<String> keywords = assetRepository.findDistinctAssetNames();

        for (String keyword : keywords) {
            List<ArticleDTO> articles = newsService.fetchNewsForKeyword(keyword);

            if (!articles.isEmpty()) {
                ArticleDTO firstArticle = articles.get(0);
                String message = String.format("New article for '%s': %s", keyword, firstArticle.getTitle());
                log.info("ALERT: {}", message);

                // --- THIS IS THE NEW LOGIC ---

                // 1. Create an in-app notification
                NotificationRequestDTO inAppRequest = new NotificationRequestDTO();
                inAppRequest.setRecipientId(admin.getId());
                inAppRequest.setMessage(message);
                notificationService.createInAppNotification(inAppRequest);

                // 2. Send an email alert
                EmailRequestDTO emailRequest = new EmailRequestDTO();
                emailRequest.setTo(admin.getEmail());
                emailRequest.setSubject("Asset News Alert: " + keyword);
                emailRequest.setBody(message + "\n\nRead more here: " + firstArticle.getUrl());
                notificationService.sendEmail(emailRequest);
            }
        }
        log.info("Finished daily news scan.");
    }
}