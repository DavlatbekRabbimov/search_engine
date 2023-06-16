package searchengine.services.service;

import searchengine.dto.result.Result;

public interface IndexingService{
    Result getResult();
    Result getResultByStartIndexingPage(String url);
    boolean isIndexing();
}
