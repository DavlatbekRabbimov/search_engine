package searchengine.controllers;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import searchengine.dto.result.Result;
import searchengine.dto.statistics.StatisticsResponse;
import searchengine.services.service.Indexing;
import searchengine.services.service.Searcher;
import searchengine.services.service.Statistics;

@Slf4j
@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ApiController {
    private final Statistics statisticsService;
    private final Indexing indexingService;
    private final Searcher searchService;

    @GetMapping("/statistics")
    public ResponseEntity<StatisticsResponse> statistics() {
        return ResponseEntity.ok(statisticsService.getStatistics());
    }

    @GetMapping("/startIndexing")
    public ResponseEntity<Result> startIndexing() {
        return ResponseEntity.ok(indexingService.startSiteIndexing());
    }

    @GetMapping("/stopIndexing")
    public ResponseEntity<Result> stopIndexing() {
        return ResponseEntity.ok(indexingService.stopSiteIndexing());
    }

    @PostMapping("/indexPage")
    public ResponseEntity<Result> indexPage(@RequestParam String url) {
        return ResponseEntity.ok(indexingService.getResultByStartIndexingPage(url));
    }

    @GetMapping("/search")
    public ResponseEntity<Result> search(String query, String site, Integer offset, Integer limit) {
        log.info(String.format("Call result to query: %s, site: %s, offset: %d, limit: %d", query, site, offset, limit));
        long start = System.currentTimeMillis();
        Result result = searchService.getResponseBySearching(query, site, offset, limit);
        long end = System.currentTimeMillis();
        log.info("query time in api: {} ms", end - start);
         return ResponseEntity.ok(result);
    }

}
