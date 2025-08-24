package org.godigit.trackwise.service.impl;

import com.google.genai.Client;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.GenerateContentResponse;
import org.godigit.trackwise.model.MaintenanceLog;
import org.godigit.trackwise.service.AiService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class AIServiceImpl implements AiService {

    private static final Logger log = LoggerFactory.getLogger(AIServiceImpl.class);
    private final Client client;
    private final String modelName = "gemini-1.5-flash"; // or "gemini-1.5-pro"

    public AIServiceImpl(@Value("${google.api.key}") String apiKey) {
        this.client = Client.builder()
                .apiKey(apiKey)
                .build();
    }

    @Override
    public String analyzeAssetPerformance(List<MaintenanceLog> logs) {
        StringBuilder prompt = new StringBuilder(
                "Analyze the maintenance history of an asset and classify its performance as GOOD, AVERAGE, or UNDERPERFORMING. Logs:\n"
        );

        logs.forEach(log -> prompt.append("- Date: ")
                .append(log.getMaintenanceDate())
                .append(", Description: ")
                .append(log.getDescription())
                .append("\n"));

        prompt.append("\nRespond with only one word: GOOD, AVERAGE, or UNDERPERFORMING.");

        try {
            GenerateContentConfig config = GenerateContentConfig.builder()
                    .build(); // no options, just defaults

            GenerateContentResponse response = client.models
                    .generateContent(modelName, prompt.toString(), config);

            String result = response.text().trim().toUpperCase();
            switch (result) {
                case "GOOD":
                case "AVERAGE":
                case "UNDERPERFORMING":
                    return result;
                default:
                    return "UNKNOWN";
            }
        } catch (Exception e) {
            log.error("Error calling Gemini API for performance analysis", e);
            return "UNKNOWN";
        }
    }

    @Override
    public boolean isNewsArticleRelevant(String title, String description) {
        String prompt = String.format(
                "You are an IT asset manager. Read the news article.\n" +
                        "Title: %s\nDescription: %s\n\n" +
                        "Does it require immediate attention (recall, security vulnerability, financial issue)? " +
                        "Answer only YES or NO.",
                title, description
        );

        try {
            GenerateContentConfig config = GenerateContentConfig.builder()
                    .build();
            GenerateContentResponse response = client.models
                    .generateContent(modelName, prompt,config);
            String result = response.toString().trim();
            return "YES".equalsIgnoreCase(result);
        } catch (Exception e) {
            log.error("Error calling Gemini API for news analysis", e);
            return false;
        }
    }
}
