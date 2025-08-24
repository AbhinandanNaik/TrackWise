package org.godigit.trackwise.dto;

import lombok.Data;

@Data
public class ArticleRequest {
    private String title;
    private String description;
    private String url;
}