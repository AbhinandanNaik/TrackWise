package org.godigit.trackwise.service.impl;

import org.godigit.trackwise.dto.ArticleRequest;
import org.godigit.trackwise.dto.NewsApiResponse;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.web.client.RestTemplate;

import java.lang.reflect.Field;
import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class NewsServiceImplTest {

    @Mock
    private RestTemplate restTemplate;

    @InjectMocks
    private NewsServiceImpl newsService;

    @BeforeEach
    void setUp() throws Exception {
        // Set the private apiKey field (normally injected via @Value)
        Field apiKeyField = NewsServiceImpl.class.getDeclaredField("apiKey");
        apiKeyField.setAccessible(true);
        apiKeyField.set(newsService, "TEST_API_KEY");
    }

    @Test
    void fetchNewsForKeyword_shouldReturnArticles_whenApiReturnsResponse() {
        // Arrange
        String keyword = "java";
        ArticleRequest a1 = new ArticleRequest();
        a1.setTitle("Java 17 Released");
        ArticleRequest a2 = new ArticleRequest();
        a2.setTitle("Spring Boot Tips");

        NewsApiResponse apiResponse = new NewsApiResponse();
        apiResponse.setArticles(List.of(a1, a2));

        when(restTemplate.getForObject(anyString(), eq(NewsApiResponse.class))).thenReturn(apiResponse);

        // Act
        List<ArticleRequest> result = newsService.fetchNewsForKeyword(keyword);

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).hasSize(2);
        assertThat(result).extracting(ArticleRequest::getTitle).containsExactly("Java 17 Released", "Spring Boot Tips");

        // verify the constructed URL contains the keyword and the api key
        ArgumentCaptor<String> urlCaptor = ArgumentCaptor.forClass(String.class);
        verify(restTemplate).getForObject(urlCaptor.capture(), eq(NewsApiResponse.class));
        String usedUrl = urlCaptor.getValue();
        assertThat(usedUrl).contains("q=" + keyword);
        assertThat(usedUrl).contains("apiKey=TEST_API_KEY");
    }

    @Test
    void fetchNewsForKeyword_shouldReturnEmptyList_whenApiReturnsNull() {
        // Arrange
        when(restTemplate.getForObject(anyString(), eq(NewsApiResponse.class))).thenReturn(null);

        // Act
        List<ArticleRequest> result = newsService.fetchNewsForKeyword("anything");

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();

        verify(restTemplate).getForObject(anyString(), eq(NewsApiResponse.class));
    }

    @Test
    void fetchNewsForKeyword_shouldReturnEmptyList_whenRestTemplateThrows() {
        // Arrange
        when(restTemplate.getForObject(anyString(), eq(NewsApiResponse.class)))
                .thenThrow(new RuntimeException("timeout"));

        // Act
        List<ArticleRequest> result = newsService.fetchNewsForKeyword("errorcase");

        // Assert
        assertThat(result).isNotNull();
        assertThat(result).isEmpty();

        verify(restTemplate).getForObject(anyString(), eq(NewsApiResponse.class));
    }
}