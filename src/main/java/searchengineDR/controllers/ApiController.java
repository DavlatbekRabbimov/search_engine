package searchengineDR.controllers;

import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import searchengineDR.dto.result.Result;
import searchengineDR.dto.statistics.StatisticsResponse;
import searchengineDR.services.service.IndexingService;
import searchengineDR.services.service.SearchService;
import searchengineDR.services.service.StatisticsService;

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
