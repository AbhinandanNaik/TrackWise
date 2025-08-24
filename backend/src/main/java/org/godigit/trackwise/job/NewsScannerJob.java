package org.godigit.trackwise.job;

import lombok.RequiredArgsConstructor;
import org.godigit.trackwise.dto.ArticleRequest;
import org.godigit.trackwise.service.AiService;
import org.godigit.trackwise.service.NewsService;
import org.godigit.trackwise.service.NotificationService;
import org.godigit.trackwise.repository.AssetRepository;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * A scheduled background job that periodically scans for external news articles
 * related to assets, uses an AI service to filter for relevance, and sends
 * alerts for critical news.
 */
@Component // Marks this class as a Spring-managed component.
@RequiredArgsConstructor // Lombok: Creates a constructor with all final fields.
public class NewsScannerJob extends QuartzJobBean {

    // A logger for printing messages to the console.
    private static final Logger log = LoggerFactory.getLogger(NewsScannerJob.class);

    // Dependencies injected by the constructor.
    private final AssetRepository assetRepository;
    private final NewsService newsService;
    private final AiService aiService;
    private final NotificationService notificationService;

    /**
     * This is the main method that gets executed by the Quartz scheduler
     * when the associated trigger fires.
     * @param context The execution context provided by Quartz.
     */
    @Override
    public void executeInternal(JobExecutionContext context) {
        log.info("Starting daily news scan for assets...");

        // 1. Get a list of unique asset names from the database to use as search keywords.
        List<String> keywords = assetRepository.findDistinctAssetNames();
        if (keywords.isEmpty()) {
            log.warn("No assets found for keyword scan. Skipping job run.");
            return; // Exit the job if there are no assets to scan for.
        }
        boolean relevantNewsFound = false;

        // 2. Loop through each keyword.
        for (String keyword : keywords) {
            // Fetch recent news articles related to the current keyword.
            List<ArticleRequest> articles = newsService.fetchNewsForKeyword(keyword);

            // 3. Loop through each found article.
            for (ArticleRequest article : articles) {

                // Delegate the complex filtering logic to the AI service.
                if (aiService.isNewsArticleRelevant(article.getTitle(), article.getDescription())) {
                    // If the AI determines the news is important, create a message.
                    relevantNewsFound = true;
                    String message = String.format("🚨 URGENT NEWS for '%s': %s", keyword, article.getTitle());
                    log.info("CRITICAL ALERT: {}", message);

                    // 4. Delegate the notification logic to the NotificationService.
                    // This will send in-app and email alerts to all admins.
                    notificationService.sendNewsAlertToAdmins(
                            article.getTitle(),
                            article.getDescription() + "\n\nRead more here: " + article.getUrl()
                    );
                }
            }
        }
        if (!relevantNewsFound) {
            log.info("Scan complete. No critical news found for any assets.");
        }
        log.info("Finished daily news scan.");
    }
}