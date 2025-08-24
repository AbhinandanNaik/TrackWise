package org.godigit.trackwise.service;

import org.godigit.trackwise.model.MaintenanceLog;
import java.util.List;

public interface AiService {
    String analyzeAssetPerformance(List<MaintenanceLog> logs);
    boolean isNewsArticleRelevant(String title, String description);
}