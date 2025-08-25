package org.godigit.trackwise.service.impl;

import com.google.genai.Client;
import com.google.genai.Models;
import com.google.genai.types.GenerateContentConfig;
import com.google.genai.types.GenerateContentResponse;
import org.godigit.trackwise.model.MaintenanceLog;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.time.LocalDate;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class AIServiceImplTest {

    private AIServiceImpl aiService;
    private Client mockClient;
    private Models mockModels;
    private GenerateContentResponse mockResponse;

    @BeforeEach
    void setUp() throws Exception {
        // normal service with fake API key
        aiService = new AIServiceImpl("dummy-key");

        // create mocks
        mockClient = mock(Client.class, withSettings().lenient());
        mockModels = mock(Models.class);
        mockResponse = mock(GenerateContentResponse.class);

        // inject mockClient into aiService via reflection
        var clientField = AIServiceImpl.class.getDeclaredField("client");
        clientField.setAccessible(true);
        clientField.set(aiService, mockClient);

        // instead of when(mockClient.models) -> use reflection to set field
        var modelsField = Client.class.getDeclaredField("models");
        modelsField.setAccessible(true);
        modelsField.set(mockClient, mockModels);
    }


    @Test
    void testAnalyzeAssetPerformance_ReturnsGood() {
        MaintenanceLog log = new MaintenanceLog();
        log.setMaintenanceDate(LocalDate.now());
        log.setDescription("Minor maintenance");

        when(mockModels.generateContent(anyString(), anyString(), any(GenerateContentConfig.class)))
                .thenReturn(mockResponse);
        when(mockResponse.text()).thenReturn("GOOD");

        String result = aiService.analyzeAssetPerformance(List.of(log));

        assertEquals("GOOD", result);
    }

    @Test
    void testIsNewsArticleRelevant_ReturnsYes() {
        when(mockModels.generateContent(anyString(), anyString(), any(GenerateContentConfig.class)))
                .thenReturn(mockResponse);
        when(mockResponse.toString()).thenReturn("YES");

        boolean relevant = aiService.isNewsArticleRelevant("Security flaw found", "Urgent recall notice");

        assertEquals(true, relevant);
    }
}
