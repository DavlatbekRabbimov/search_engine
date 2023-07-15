package FunctionalTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import searchengine.config.SiteConfig;
import searchengine.dto.result.Result;
import searchengine.model.repo.IndexRepo;
import searchengine.model.repo.LemmaRepo;
import searchengine.model.repo.PageRepo;
import searchengine.model.repo.SiteRepo;
import searchengine.services.searchtools.SiteTool;
import searchengine.services.serviceimpl.DbService;
import searchengine.services.serviceimpl.IndexingServiceImpl;

import java.util.concurrent.ForkJoinPool;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;
@ExtendWith(MockitoExtension.class)
public class IndexingServiceImplTest {

    @Mock
    private SiteRepo siteRepo;
    @Mock
    private PageRepo pageRepo;
    @Mock
    private LemmaRepo lemmaRepo;
    @Mock
    private IndexRepo indexRepo;
    @Mock(name = "search_engine")
    private DbService dbService;
    @Mock
    private SiteConfig siteConfig;
    @Mock
    private SiteTool siteTool;
    @Mock
    ForkJoinPool pool;
    private IndexingServiceImpl indexingService;
    private Result result;

    @BeforeEach
    public void setUp() {
        MockitoAnnotations.openMocks(this);
        this.indexingService = new IndexingServiceImpl(siteRepo, pageRepo, lemmaRepo, indexRepo, dbService, siteConfig, siteTool);
        this.indexingService.setPool(pool);
    }

    @Test
    public void testGetResultIfNotIndexing() {
        when(pool.getActiveThreadCount()).thenReturn(0);

        result = indexingService.getResult();
        assertTrue(result.isResult());
        assertEquals("Индексация не запущена", result.getError());
    }

    @Test
    public void testGetResultIfIsIndexing() {
        when(pool.getActiveThreadCount()).thenReturn(1);

        result = indexingService.getResult();
        assertFalse(result.isResult());
        assertEquals("Индексация уже запущена", result.getError());
    }

    @Test
    public void testGetResultByStartIndexingPageIsUrlFound() {

        String url = "https://www.svetlovka.ru";

        when(siteTool.isUrlFound(url)).thenReturn(true);

        result = indexingService.getResultByStartIndexingPage(url);
        assertTrue(result.isResult());
        verify(siteTool).isUrlFound(url);
    }

    @Test
    public void testGetResultByStartIndexingPageIsUrlNotFound() {
        result = indexingService.getResultByStartIndexingPage("https://www.google.com");
        assertEquals("Данная страница находится за пределами сайтов, указанных в конфигурационном файле", result.getError());
    }

    @Test
    public void testGetResultByStartIndexingPageIsEmpty() {

        result = indexingService.getResultByStartIndexingPage("");
        assertEquals("Ошибка: Пустое поле. Пожалуйста, введите адрес страницы сайта, указанного в конфигурационном файле", result.getError());
    }

}
