package searchengine.services.service;

import searchengine.dto.result.Result;


public interface SearchService {
    Result getResponseBySearching(String query, String site, Integer offset, Integer limit);
}
