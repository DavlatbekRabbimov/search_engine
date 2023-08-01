package searchengine.services.serviceimpl;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.stereotype.Service;
import searchengine.dto.result.Result;
import searchengine.dto.statistics.SearchStatistics;
import searchengine.model.entity.Lemmas;
import searchengine.model.entity.Pages;
import searchengine.model.repo.LemmaRepo;
import searchengine.services.searchtools.*;
import searchengine.services.service.Searcher;

import java.util.Collections;
import java.util.List;
import java.util.Set;

@Slf4j
@RequiredArgsConstructor
@Service
public class SearcherService implements Searcher {

    private final LemmaRepo lemmaRepo;
    private final LemmatizerService lemmatizer;
    private final LemmaTool lemmaTool;
    private final SiteTool siteTool;
    private final ContentTool contentTool;
    private final TitleTool titleTool;
    private final SnippetTool snippetTool;
    private final RelevanceTool relevanceTool;

    @Override
    @Cacheable(value = "searchCache", key = "{#query, #site, #offset, #limit}")
    public Result getResponseBySearching(String query, String site, Integer offset, Integer limit) {

        Result result = new Result();
        Set<String> normalWords = lemmatizer.getNormalWords(query);

        List<Lemmas> queryList = lemmaRepo.findByLemmaSet(normalWords).orElse(Collections.emptyList());

        if (query == null || query.isEmpty()) {
            result.setError("Задан пустой поисковый запрос");
            return result;
        }

        if (queryList.isEmpty()) {
            result.setError("Страниц, удовлетворяющих запрос, не существует.");
            return result;
        }

        if (site != null && !siteTool.isUrlFoundInConfigFile(site)) {
            result.setError("Данного сайта нет в списке.");
            return result;
        }

        Set<Pages> pages = lemmaTool.getFilteredLemmas(queryList, site);
        List<SearchStatistics> searchDataList = createDataList(queryList, pages);

        result.setCount(searchDataList.size());
        result.setResult(true);

        if (offset >= searchDataList.size()) {
            result.setData(Collections.emptyList());
        } else {
            result.setData(searchDataList.subList(offset, Math.min(offset + limit, searchDataList.size())));
        }
        log.info("searchCache of query: {} | site: {} | offset: {} | limit: {} |",
                query, site != null ? site : "All sites", offset, limit);

        return result;
    }

    public List<SearchStatistics> createDataList(List<Lemmas> lemmaList, Set<Pages> pageSet) {
        return pageSet.stream()
                .map(page -> {
                    String siteUrl = page.getSite().getUrl();
                    String siteUrn = page.getSite().getName();
                    String siteUri = page.getPath();
                    String titles = titleTool.getTitle(page.getContent());
                    String contents = contentTool.getProcessedContent(page.getContent());
                    String snippets = snippetTool.generatedContentWithLemmaList(contents, lemmaList);
                    float pageRank = relevanceTool.getPageRank(page.getId());
                    return new SearchStatistics(siteUrl, siteUrn, siteUri, titles, snippets, pageRank);
                })
                .sorted(Collections.reverseOrder())
                .toList();
    }
}
