package FunctionalTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import searchengine.dto.result.Result;
import searchengine.model.entity.Lemmas;
import searchengine.model.entity.Pages;
import searchengine.model.entity.Sites;
import searchengine.model.repo.LemmaRepo;
import searchengine.services.searchtools.*;
import searchengine.services.serviceimpl.LemmatizerService;
import searchengine.services.serviceimpl.SearcherService;

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
    private LemmatizerService lemmatizer;
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

    private SearcherService searchService;
    private Result result;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.initMocks(this);
        this.searchService = new SearcherService(lemmaRepo, lemmatizer, lemmaTool, siteTool, contentTool, titleTool, snippetTool, relevanceTool);
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
        Optional<List<Lemmas>> queryListIsEmpty = Optional.of(Collections.emptyList());

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
        Optional<List<Lemmas>> queryListHasValue = Optional.of(List.of(new Lemmas(new Sites(null, null, "", site, ""), "дом", 0)));

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
        Lemmas lemma = new Lemmas(null, normalWord, 0);
        List<Lemmas> queryListHasValue = List.of(lemma);

        Sites site = new Sites(null, null, "", url, "");
        Set<Pages> pages = new HashSet<>(List.of(new Pages(site, "", 0, "")));

        when(lemmatizer.getNormalWords(searchQuery)).thenReturn(normalWords);
        when(lemmaRepo.findByLemmaSet(normalWords)).thenReturn(Optional.of(queryListHasValue));
        when(siteTool.isUrlFoundInConfigFile(site.getUrl())).thenReturn(true);
        when(lemmaTool.getFilteredLemmas(queryListHasValue, site.getUrl())).thenReturn(pages);

        result = searchService.getResponseBySearching(searchQuery, site.getUrl(), 0, 20);

        assertEquals(1, result.getCount());
        assertTrue(result.isResult());

    }

}

