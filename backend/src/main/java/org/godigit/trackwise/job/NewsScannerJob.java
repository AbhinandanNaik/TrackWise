package org.godigit.trackwise.job;

import lombok.RequiredArgsConstructor;
import org.godigit.trackwise.dto.ArticleRequest;
import org.godigit.trackwise.model.Employee;
import org.godigit.trackwise.repository.AssetRepository;
import org.godigit.trackwise.repository.EmployeeRepository;
import org.godigit.trackwise.service.impl.NewsServiceImpl;
import org.godigit.trackwise.service.NotificationService;
import org.godigit.trackwise.service.impl.OpenAIServiceImpl;
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
    private final NewsServiceImpl newsService;
    private final OpenAIServiceImpl openAIServiceImpl;
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
            List<ArticleRequest> articles = newsService.fetchNewsForKeyword(keyword);

            for (ArticleRequest article : articles) {
                // Use AI to filter for relevance
                if (openAIServiceImpl.isNewsArticleRelevant(article.getTitle(), article.getDescription())) {
                    String message = String.format("URGENT NEWS for '%s': %s", keyword, article.getTitle());
                    log.info("CRITICAL ALERT: {}", message);

                    // Create in-app notification and send email for the filtered article...
                }
            }
        }
        log.info("Finished daily news scan.");
    }
}