package org.godigit.trackwise.service.impl;

import com.openai.client.OpenAIClient;
import com.openai.client.okhttp.OpenAIOkHttpClient;
import com.openai.models.ChatModel;
import com.openai.models.chat.completions.*;
import org.godigit.trackwise.model.MaintenanceLog;
import org.godigit.trackwise.service.OpenAIService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class OpenAIServiceImpl implements OpenAIService {

    private final OpenAIClient client;

    public OpenAIServiceImpl(@Value("${openai.api.key}") String apiKey) {
        this.client = OpenAIOkHttpClient.builder()
                .apiKey(apiKey)
                .build();
    }

    public String analyzeAssetPerformance(List<MaintenanceLog> logs) {
        StringBuilder promptText = new StringBuilder(
                "Analyze the maintenance history of an asset and classify its performance as " +
                        "GOOD, AVERAGE, or UNDERPERFORMING. Here are the logs:\n"
        );

        logs.forEach(log -> {
            promptText.append("- Date: ").append(log.getMaintenanceDate())
                    .append(", Description: ").append(log.getDescription()).append("\n");
        });
        promptText.append("\nRespond with only one word: GOOD, AVERAGE, or UNDERPERFORMING.");

        ChatCompletion completion = client.chat().completions().create(
                ChatCompletionCreateParams.builder()
                        .model(ChatModel.GPT_4O_MINI)
                        .addMessage(
                                ChatCompletionSystemMessageParam.builder()
                                        .content("You are an asset performance analyst.")
                                        .build()
                        )
                        .addMessage(
                                ChatCompletionUserMessageParam.builder()
                                        .content(promptText.toString())
                                        .build()
                        )
                        .build()
        );

        return completion.choices().get(0).message().content().orElse("UNKNOWN");
    }

    public boolean isNewsArticleRelevant(String title, String description) {
        String promptText = String.format(
                "You are an IT asset manager. Read the following news article title and description. " +
                        "Is this article something that requires immediate attention, such as a product recall, " +
                        "major security vulnerability, or significant financial news?\n\n" +
                        "Title: %s\nDescription: %s\n\n" +
                        "Respond with only 'YES' or 'NO'.",
                title, description
        );

        ChatCompletion completion = client.chat().completions().create(
                ChatCompletionCreateParams.builder()
                        .model(ChatModel.GPT_4O_MINI)
                        .addMessage(
                                ChatCompletionSystemMessageParam.builder()
                                        .content("You are an intelligent news filter for IT professionals.")
                                        .build()
                        )
                        .addMessage(
                                ChatCompletionUserMessageParam.builder()
                                        .content(promptText)
                                        .build()
                        )
                        .build()
        );

        String response = completion.choices().get(0).message().content().orElse("NO");
        return "YES".equalsIgnoreCase(response.trim());
    }
}
