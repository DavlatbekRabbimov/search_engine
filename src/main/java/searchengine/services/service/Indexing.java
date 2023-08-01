package searchengine.services.service;

import searchengine.dto.result.Result;

public interface Indexing {

    Result getResultByStartIndexingPage(String url);
    Result startSiteIndexing();
    Result stopSiteIndexing();
    boolean isIndexing();
}
