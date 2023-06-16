package searchengine.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import searchengine.dto.result.Result;
import searchengine.dto.statistics.StatisticsResponse;
import searchengine.services.service.IndexingService;
import searchengine.services.service.SearchService;
import searchengine.services.service.StatisticsService;

@RestController
@RequestMapping("/api")
@RequiredArgsConstructor
public class ApiController {
    private final StatisticsService statisticsService;
    private final IndexingService indexingService;
    private final SearchService searchService;

    @GetMapping("/statistics")
    public ResponseEntity<StatisticsResponse> statistics() {
        return ResponseEntity.ok(statisticsService.getStatistics());
    }

    @GetMapping("/startIndexing")
    public ResponseEntity<Result> startIndexing() {
        return ResponseEntity.ok(indexingService.getResult());
    }

    @GetMapping("/stopIndexing")
    public ResponseEntity<Result> stopIndexing() {
        return ResponseEntity.ok(indexingService.getResult());
    }

    @PostMapping("/indexPage")
    public ResponseEntity<Result> indexPage(@RequestParam String url)  {
        return ResponseEntity.ok(indexingService.getResultByStartIndexingPage(url));
    }

    @GetMapping("/search")
    public ResponseEntity<Result> search(String query, String site, Integer offset, Integer limit) {
        return ResponseEntity.ok(searchService.getResponseBySearching(query, site, offset, limit));
    }

}
