package org.godigit.trackwise.service;

import org.godigit.trackwise.dto.ArticleDTO;
import org.godigit.trackwise.dto.NewsApiResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.Collections;
import java.util.List;

@Service
public class NewsService {

    private final RestTemplate restTemplate;

    @Value("${news.api.key}")
    private String apiKey;

    public NewsService(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    public List<ArticleDTO> fetchNewsForKeyword(String keyword) {
        // Example URL for NewsAPI.org
        String url = "https://newsapi.org/v2/everything?q=" + keyword + "&apiKey=" + apiKey;

        try {
            NewsApiResponse response = restTemplate.getForObject(url, NewsApiResponse.class);
            return response != null ? response.getArticles() : Collections.emptyList();
        } catch (Exception e) {
            // Log the error
            return Collections.emptyList();
        }
    }
}