package org.godigit.trackwise.dto;

import lombok.Data;

import java.util.List;

@Data
public class NewsApiResponse {
    private List<ArticleDTO> articles;
}