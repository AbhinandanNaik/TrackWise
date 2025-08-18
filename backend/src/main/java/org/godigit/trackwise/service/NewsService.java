//package org.godigit.trackwise.service;
//
//import org.springframework.stereotype.Service;
//
//@Service
//public class NewsService {
//
//    private final WebClient webClient = WebClient.create("https://newsapi.org/v2");
//
//    public List<Article> getVendorNews(String vendorName) {
//        return webClient.get()
//                .uri(uriBuilder -> uriBuilder
//                        .path("/everything")
//                        .queryParam("q", vendorName)
//                        .queryParam("apiKey", "YOUR_API_KEY")
//                        .build())
//                .retrieve()
//                .bodyToMono(NewsApiResponse.class)
//                .block()
//                .getArticles();
//    }
//}
