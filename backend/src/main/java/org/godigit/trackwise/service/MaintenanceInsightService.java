//package org.godigit.trackwise.service;
//
//import org.springframework.stereotype.Service;
//
//@Service
//public class MaintenanceInsightService {
//
//    private final WebClient webClient = WebClient.builder()
//            .baseUrl("https://api.openai.com/v1")
//            .defaultHeader("Authorization", "Bearer YOUR_API_KEY")
//            .build();
//
//    public String getSuggestions(String maintenanceLogSummary) {
//        return webClient.post()
//                .uri("/chat/completions")
//                .bodyValue(Map.of(
//                        "model", "gpt-4",
//                        "messages", List.of(
//                                Map.of("role", "system", "content", "You are a maintenance expert."),
//                                Map.of("role", "user", "content", "Here is a maintenance log: " + maintenanceLogSummary)
//                        )
//                ))
//                .retrieve()
//                .bodyToMono(String.class)
//                .block();
//    }
//}
