package searchengine.services.serviceimpl;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import searchengine.dto.statistics.SearchStatistics;
import searchengine.dto.result.Result;
import searchengine.model.entity.Lemma;
import searchengine.model.entity.Page;
import searchengine.model.repo.LemmaRepo;
import searchengine.model.repo.PageRepo;
import searchengine.services.searchtools.SnippetTool;
import searchengine.services.searchtools.*;
import searchengine.services.service.SearchService;

import java.util.*;

@Service
@RequiredArgsConstructor
public class SearchServiceImpl implements SearchService {

    private final LemmaRepo lemmaRepo;
    private final LemmatizerServiceImpl lemmatizer;
    private final LemmaTool lemmaTool;
    private final SiteTool siteTool;
    private final ContentTool contentTool;
    private final TitleTool titleTool;
    private final SnippetTool snippetTool;
    private final RelevanceTool relevanceTool;
    private final PageRepo pageRepo;

    @Override
    public Result getResponseBySearching(String query, String site, Integer offset, Integer limit) {
        Result result = new Result();
        Set<String> normalWords = lemmatizer.getNormalWords(query);
        List<Lemma> queryList = lemmaRepo.findByLemmaSet(normalWords).orElse(Collections.emptyList());

        if (query.isEmpty()) {
            result.setError("Задан пустой поисковый запрос");
            return result;
        }
        if (!query.isEmpty() && queryList.isEmpty()) {
            result.setError("Страниц, удовлетворяющих запрос, не существует.");
            return result;
        }

        if (site != null && !siteTool.isUrlFoundInConfigFile(site)) {
            result.setError("Данного сайта нет в списке.");
            return result;
        }

        Set<Page> pages = lemmaTool.getFilteredLemmas(queryList, site);
        if (!pageRepo.findCodeGreaterThanOrEqual400().isEmpty()) {
            result.setError("Ошибка: сервер не отвечает");
        }

        List<SearchStatistics> searchList = createDataList(queryList, pages);
        result.setCount(searchList.size());
        result.setResult(true);

        if (offset >= searchList.size()) {
            result.setData(Collections.emptyList());
        } else {
            result.setData(searchList.subList(offset, Math.min(offset + limit, searchList.size())));
        }
        return result;
    }

    private List<SearchStatistics> createDataList(List<Lemma> lemmaList, Set<Page> pageSet) {
        return pageSet.stream()
                .map(page -> {
                    String siteUrl = page.getSite().getUrl();
                    String siteUrn = page.getSite().getName();
                    String siteUri = page.getPath();
                    String titles = titleTool.getTitle(page.getContent());
                    String contents = contentTool.getProcessedContent(page.getContent());
                    String snippets = snippetTool.generatedContentWithLemmaList(contents, lemmaList);
                    float pageRelevance = relevanceTool.getRelevance(page);
                    return new SearchStatistics(siteUrl, siteUrn, siteUri, titles, snippets, pageRelevance);
                })
                .sorted(Collections.reverseOrder())
                .toList();
    }
}
