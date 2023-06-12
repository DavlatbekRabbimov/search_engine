package searchengineDR.services.service;

import searchengineDR.dto.result.Result;

public interface IndexingService{
    Result getResult();
    Result getResultByStartIndexingPage(String url);
    boolean isIndexing();
}
