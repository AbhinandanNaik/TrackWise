package org.godigit.trackwise.service;

import org.godigit.trackwise.model.MaintenanceLog;

import java.util.List;

public interface OpenAIService {

    public String analyzeAssetPerformance(List<MaintenanceLog> logs);

    public boolean isNewsArticleRelevant(String title, String description);

}
