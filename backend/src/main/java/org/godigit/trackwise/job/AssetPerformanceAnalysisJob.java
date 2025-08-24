package org.godigit.trackwise.job;

import lombok.RequiredArgsConstructor;
import org.godigit.trackwise.model.Asset;
import org.godigit.trackwise.model.MaintenanceLog;
import org.godigit.trackwise.repository.AssetRepository;
import org.godigit.trackwise.repository.MaintenanceLogRepository;
import org.godigit.trackwise.service.AiService;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * A scheduled background job that analyzes the performance of all assets
 * based on their maintenance history. This job is managed by the Quartz scheduler.
 */
@Component // Marks this class as a Spring-managed component.
@RequiredArgsConstructor // Lombok: Creates a constructor with all final fields.
public class AssetPerformanceAnalysisJob extends QuartzJobBean {

    // A logger for printing messages to the console.
    private static final Logger log = LoggerFactory.getLogger(AssetPerformanceAnalysisJob.class);

    // Dependencies injected by the constructor.
    private final AssetRepository assetRepository;
    private final MaintenanceLogRepository maintenanceLogRepository;
    private final AiService aiService;

    /**
     * This is the main method that gets executed by the Quartz scheduler
     * when the associated trigger fires.
     * @param context The execution context provided by Quartz.
     */
    @Override
    public void executeInternal(JobExecutionContext context) {
        log.info("Starting weekly asset performance analysis...");

        // 1. Fetch all assets from the database.
        List<Asset> assets = assetRepository.findAll();

        // 2. Loop through each asset to analyze its performance.
        for (Asset asset : assets) {
            // Fetch all maintenance logs for the current asset.
            List<MaintenanceLog> logs = maintenanceLogRepository.findByAssetId(asset.getId());

            // Only perform analysis if the asset has a maintenance history.
            if (!logs.isEmpty()) {
                log.info("Analyzing performance for asset: {}", asset.getName());

                // 3. Delegate the complex analysis logic to the AI service.
                String performance = aiService.analyzeAssetPerformance(logs);

                // 4. Update the asset's performance status with the result from the AI.
                asset.setPerformanceStatus(performance);

                // 5. Save the updated asset back to the database.
                assetRepository.save(asset);

                log.info("...Performance for '{}' set to: {}", asset.getName(), performance);
            }
        }

        log.info("Asset performance analysis complete.");
    }
}