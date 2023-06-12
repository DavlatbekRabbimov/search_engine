package searchengineDR.services.service;

import searchengineDR.dto.result.Result;


public interface SearchService {
    Result getResponseBySearching(String query, String site, Integer offset, Integer limit);
}
