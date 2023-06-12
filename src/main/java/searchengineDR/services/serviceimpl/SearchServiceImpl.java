package searchengineDR.services.serviceimpl;


import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import searchengineDR.dto.statistics.SearchStatistics;
import searchengineDR.dto.result.Result;
import searchengineDR.model.entity.Lemma;
import searchengineDR.model.entity.Page;
import searchengineDR.model.repo.LemmaRepo;
import searchengineDR.model.repo.PageRepo;
import searchengineDR.services.searchtools.SnippetTool;
import searchengineDR.services.searchtools.*;
import searchengineDR.services.service.SearchService;

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

        if (site != null && !siteTool.isUrlOrPageFound(site)) {
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
                    String site = page.getSite().getUrl();
                    String siteName = page.getSite().getName();
                    String uri = page.getPath();
                    String title = titleTool.getTitle(page.getContent());
                    String text = contentTool.getProcessedContent(page.getContent());
                    String snippet = snippetTool.generateSnippetWithLemmas(text, lemmaList);
                    float relevance = relevanceTool.getRelevance(page);
                    return new SearchStatistics(site, siteName, uri, title, snippet, relevance);
                })
                .sorted(Collections.reverseOrder())
                .toList();
    }
}
