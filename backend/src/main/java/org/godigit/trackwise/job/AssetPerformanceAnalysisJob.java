package org.godigit.trackwise.job;

import lombok.RequiredArgsConstructor;
import org.godigit.trackwise.model.Asset;
import org.godigit.trackwise.model.MaintenanceLog;
import org.godigit.trackwise.repository.AssetRepository;
import org.godigit.trackwise.repository.MaintenanceLogRepository;
import org.godigit.trackwise.service.impl.OpenAIServiceImpl;
import org.quartz.JobExecutionContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.scheduling.quartz.QuartzJobBean;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
@RequiredArgsConstructor
public class AssetPerformanceAnalysisJob extends QuartzJobBean {

    private static final Logger log = LoggerFactory.getLogger(AssetPerformanceAnalysisJob.class);
    private final AssetRepository assetRepository;
    private final MaintenanceLogRepository maintenanceLogRepository;
    private final OpenAIServiceImpl openAIServiceImpl;

    @Override
    public void executeInternal(JobExecutionContext context) {
        log.info("Starting weekly asset performance analysis...");
        List<Asset> assets = assetRepository.findAll();
        for (Asset asset : assets) {
            List<MaintenanceLog> logs = maintenanceLogRepository.findByAssetId(asset.getId());
            if (!logs.isEmpty()) {
                log.info("Analyzing performance for asset: {}", asset.getName());
                String performance = openAIServiceImpl.analyzeAssetPerformance(logs);
                asset.setPerformanceStatus(performance);
                assetRepository.save(asset);
                log.info("...Performance for '{}' set to: {}", asset.getName(), performance);
            }
        }
        log.info("Asset performance analysis complete.");
    }
}