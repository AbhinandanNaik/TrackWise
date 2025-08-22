package org.godigit.trackwise.job;

import lombok.RequiredArgsConstructor;

import org.godigit.trackwise.dto.ArticleRequest;
import org.godigit.trackwise.repository.AssetRepository;
import org.godigit.trackwise.service.AiService;
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
    private final NewsService newsService;
    private final AiService aiService;
    private final NotificationService notificationService;

    @Override
    public void executeInternal(JobExecutionContext context) {
        log.info("Starting daily news scan for assets...");

        List<String> keywords = assetRepository.findDistinctAssetNames();
        if (keywords.isEmpty()) {
            log.warn("No assets found for keyword scan.");
            return;
        }

        for (String keyword : keywords) {
            List<ArticleRequest> articles = newsService.fetchNewsForKeyword(keyword);

            for (ArticleRequest article : articles) {
                if (aiService.isNewsArticleRelevant(article.getTitle(), article.getDescription())) {
                    String message = String.format("🚨 URGENT NEWS for '%s': %s", keyword, article.getTitle());
                    log.info("CRITICAL ALERT: {}", message);

                    // Send notification to all admins
                    notificationService.sendNewsAlertToAdmins(
                            article.getTitle(),
                            article.getDescription() + "\n\nRead more here: " + article.getUrl()
                    );
                }
            }
        }
        log.info("Finished daily news scan.");
    }
}