package FunctionalTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import searchengine.dto.result.Result;
import searchengine.model.entity.Lemma;
import searchengine.model.entity.Page;
import searchengine.model.entity.Site;
import searchengine.model.repo.LemmaRepo;
import searchengine.services.searchtools.*;
import searchengine.services.serviceimpl.LemmatizerServiceImpl;
import searchengine.services.serviceimpl.SearchServiceImpl;

import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
public class SearchServiceImplTest {

    @Mock
    private LemmaRepo lemmaRepo;
    @Mock
    private LemmatizerServiceImpl lemmatizer;
    @Mock
    private LemmaTool lemmaTool;
    @Mock
    private SiteTool siteTool;
    @Mock
    private ContentTool contentTool;
    @Mock
    private TitleTool titleTool;
    @Mock
    private SnippetTool snippetTool;
    @Mock
    private RelevanceTool relevanceTool;

    private SearchServiceImpl searchService;
    private Result result;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        this.searchService = new SearchServiceImpl(lemmaRepo, lemmatizer, lemmaTool, siteTool, contentTool, titleTool, snippetTool, relevanceTool);
    }

    @Test
    public void testGetResponseBySearchingQueryIsEmpty() {

        result = searchService.getResponseBySearching("", "https://plaback.ru", 0, 20);

        assertEquals("Задан пустой поисковый запрос", result.getError());
    }

    @Test
    public void testGetResponseBySearchingQueryIsNotEmpty() {

        String searchQuery = "дома";
        LinkedHashSet<String> normalWords = new LinkedHashSet<>(Collections.singleton("дом"));
        Optional<List<Lemma>> queryListIsEmpty = Optional.of(Collections.emptyList());

        when(lemmatizer.getNormalWords(searchQuery)).thenReturn(normalWords);
        when(lemmaRepo.findByLemmaSet(any())).thenReturn(queryListIsEmpty);

        result = searchService.getResponseBySearching(searchQuery, "", 0, 20);

        assertEquals("Страниц, удовлетворяющих запрос, не существует.", result.getError());

    }

    @Test
    public void testGetResponseBySearchingSiteIsNotNullAndSitesAreNotInConFile() {

        String searchQuery = "дома";
        LinkedHashSet<String> normalWords = new LinkedHashSet<>(Collections.singleton("дом"));
        String site = "https://playback.ru";
        Optional<List<Lemma>> queryListHasValue = Optional.of(List.of(new Lemma(new Site(null, null, "", site, ""), "дом", 0)));

        when(lemmatizer.getNormalWords(searchQuery)).thenReturn(normalWords);
        when(lemmaRepo.findByLemmaSet(normalWords)).thenReturn(queryListHasValue);
        when(siteTool.isUrlFoundInConfigFile(site)).thenReturn(false);

        result = searchService.getResponseBySearching(searchQuery, site, 0, 20);

        assertEquals("Данного сайта нет в списке.", result.getError());

    }

    @Test
    public void testGetResponseBySearching() {

        String searchQuery = "дома";
        String normalWord = "дом";
        String url = "https://playback.ru";

        LinkedHashSet<String> normalWords = new LinkedHashSet<>(Collections.singleton(normalWord));
        Lemma lemma = new Lemma(null, normalWord, 0);
        List<Lemma> queryListHasValue = List.of(lemma);

        Site site = new Site(null, null, "", url, "");
        Set<Page> pages = new HashSet<>(List.of(new Page(site, "", 0, "")));

        when(lemmatizer.getNormalWords(searchQuery)).thenReturn(normalWords);
        when(lemmaRepo.findByLemmaSet(normalWords)).thenReturn(Optional.of(queryListHasValue));
        when(siteTool.isUrlFoundInConfigFile(site.getUrl())).thenReturn(true);
        when(lemmaTool.getFilteredLemmas(queryListHasValue, site.getUrl())).thenReturn(pages);

        result = searchService.getResponseBySearching(searchQuery, site.getUrl(), 0, 20);

        assertEquals(1, result.getCount());
        assertTrue(result.isResult());

    }

}

