//package org.godigit.trackwise.service.impl;
//
//import com.google.genai.Client;
//import com.google.genai.types.GenerateContentConfig;
//import com.google.genai.types.GenerateContentResponse;
//import org.godigit.trackwise.model.MaintenanceLog;
//import org.junit.jupiter.api.BeforeEach;
//import org.junit.jupiter.api.Test;
//import org.junit.jupiter.api.extension.ExtendWith;
//import org.mockito.Mock;
//import org.mockito.junit.jupiter.MockitoExtension;
//
//import java.lang.reflect.Field;
//import java.time.LocalDate;
//import java.util.List;
//
//import static org.assertj.core.api.Assertions.*;
//import static org.mockito.ArgumentMatchers.*;
//import static org.mockito.Mockito.*;
//
///**
// * Unit tests for AIServiceImpl. We construct a real AIServiceImpl instance (using a dummy API key),
// * then replace its private final 'client' field with a mocked Client whose 'models' field is also mocked.
// */
//@ExtendWith(MockitoExtension.class)
//class AIServiceImplTest {
//
//    // We'll create mocks for the Client and its Models implementation.
//    @Mock
//    private Client mockClient;
//
//    @Mock
//    private Client.Models mockModels;
//
//    @Mock
//    private GenerateContentResponse mockResponse;
//
//    private AIServiceImpl aiService;
//
//    @BeforeEach
//    void setUp() throws Exception {
//        // Create the service with a dummy api key (constructor builds a real Client internally,
//        // but we'll overwrite the private final client field with our mock).
//        aiService = new AIServiceImpl("DUMMY_KEY");
//
//        // Prepare the mock client: set its 'models' field to our mockModels instance via reflection.
//        Field clientField = AIServiceImpl.class.getDeclaredField("client");
//        clientField.setAccessible(true);
//
//        // Ensure the mockClient has its 'models' field set to mockModels (Client has a public final field 'models').
//        // We set the 'models' field on the mockClient instance first.
//        Field modelsFieldOnClient = Client.class.getDeclaredField("models");
//        modelsFieldOnClient.setAccessible(true);
//        modelsFieldOnClient.set(mockClient, mockModels);
//
//        // Now inject the mockClient into the aiService instance.
//        clientField.set(aiService, mockClient);
//    }
//
//    @Test
//    void analyzeAssetPerformance_shouldReturnGood_whenModelRespondsGood() throws Exception {
//        // Arrange - create sample logs
//        MaintenanceLog l1 = new MaintenanceLog();
//        l1.setMaintenanceDate(LocalDate.of(2024, 1, 1));
//        l1.setDescription("Routine check - no issues");
//
//        // Stub model call to return a response whose text() returns "GOOD"
//        when(mockModels.generateContent(anyString(), contains("Routine check"), any(GenerateContentConfig.class)))
//                .thenReturn(mockResponse);
//        when(mockResponse.text()).thenReturn("GOOD");
//
//        // Act
//        String result = aiService.analyzeAssetPerformance(List.of(l1));
//
//        // Assert
//        assertThat(result).isEqualTo("GOOD");
//        verify(mockModels).generateContent(anyString(), anyString(), any(GenerateContentConfig.class));
//    }
//
//    @Test
//    void analyzeAssetPerformance_shouldReturnUnknown_onUnexpectedResponse() {
//        // Arrange
//        MaintenanceLog l1 = new MaintenanceLog();
//        l1.setMaintenanceDate(LocalDate.of(2024, 1, 1));
//        l1.setDescription("Some failures observed");
//
//        when(mockModels.generateContent(anyString(), anyString(), any(GenerateContentConfig.class)))
//                .thenReturn(mockResponse);
//        // model returns unrecognized text
//        when(mockResponse.text()).thenReturn("SOMETHING_ELSE");
//
//        // Act
//        String result = aiService.analyzeAssetPerformance(List.of(l1));
//
//        // Assert
//        assertThat(result).isEqualTo("UNKNOWN");
//    }
//
//    @Test
//    void analyzeAssetPerformance_shouldReturnUnknown_whenModelThrows() {
//        // Arrange
//        MaintenanceLog l1 = new MaintenanceLog();
//        l1.setMaintenanceDate(LocalDate.of(2024, 1, 1));
//        l1.setDescription("Error case");
//
//        when(mockModels.generateContent(anyString(), anyString(), any(GenerateContentConfig.class)))
//                .thenThrow(new RuntimeException("api error"));
//
//        // Act
//        String result = aiService.analyzeAssetPerformance(List.of(l1));
//
//        // Assert
//        assertThat(result).isEqualTo("UNKNOWN");
//    }
//
//    @Test
//    void isNewsArticleRelevant_shouldReturnTrue_whenResponseIsYes() {
//        // Arrange
//        String title = "Critical vulnerability found";
//        String desc = "Exploit in firmware leads to remote code execution";
//
//        when(mockModels.generateContent(anyString(), contains("Does it require immediate attention"), any(GenerateContentConfig.class)))
//                .thenReturn(mockResponse);
//
//        // The implementation uses response.toString().trim() to get answer. Stub toString().
//        when(mockResponse.toString()).thenReturn("YES");
//
//        // Act
//        boolean relevant = aiService.isNewsArticleRelevant(title, desc);
//
//        // Assert
//        assertThat(relevant).isTrue();
//    }
//
//    @Test
//    void isNewsArticleRelevant_shouldReturnFalse_whenResponseIsNo() {
//        // Arrange
//        when(mockModels.generateContent(anyString(), anyString(), any(GenerateContentConfig.class)))
//                .thenReturn(mockResponse);
//        when(mockResponse.toString()).thenReturn("NO");
//
//        boolean relevant = aiService.isNewsArticleRelevant("Minor update", "Not security related");
//        assertThat(relevant).isFalse();
//    }
//
//    @Test
//    void isNewsArticleRelevant_shouldReturnFalse_whenModelThrows() {
//        // Arrange
//        when(mockModels.generateContent(anyString(), anyString(), any(GenerateContentConfig.class)))
//                .thenThrow(new RuntimeException("api down"));
//
//        // Act
//        boolean relevant = aiService.isNewsArticleRelevant("Anything", "Anything");
//
//        // Assert
//        assertThat(relevant).isFalse();
//    }
//}