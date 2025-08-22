package org.godigit.trackwise.service;

import org.godigit.trackwise.dto.ArticleRequest;

import java.util.List;

public interface NewsService {

    public List<ArticleRequest> fetchNewsForKeyword(String keyword);
}
