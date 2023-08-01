
package searchengine.services.serviceimpl;

import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.Jsoup;
import org.springframework.stereotype.Service;

import searchengine.config.SiteConfig;
import searchengine.dto.result.Result;
import searchengine.model.*;

import searchengine.model.entity.Sites;
import searchengine.model.repo.*;
import searchengine.services.parser.SiteRecursiveTask;
import searchengine.services.searchtools.SiteTool;
import searchengine.services.service.Indexing;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;

@RequiredArgsConstructor
@Setter
@Getter
@Slf4j
@Service
public class IndexingService implements Indexing {

    private final SiteRepo siteRepo;
    private final PageRepo pageRepo;
    private final LemmaRepo lemmaRepo;
    private final IndexRepo indexRepo;
    private final DbService db;
    private final SiteConfig siteConfig;
    private final SiteTool siteTool;
    private ForkJoinPool pool;

    @SneakyThrows
    @Override
    public Result getResultByStartIndexingPage(String url) {
        Result result = new Result();
        if (siteTool.isUrlFound(url)) {
            result.setResult(true);
            startIndexingPage(url);
        }
        else {
            result.setError("Данная страница находится за пределами сайтов, указанных в конфигурационном файле");
        }
        if (url.isEmpty()) {
            result.setError("Ошибка: Пустое поле. Пожалуйста, введите адрес страницы сайта, указанного в конфигурационном файле");
        }
        return result;
    }

    @Override
    public Result startSiteIndexing() {
        Result result = new Result();
        if(!isIndexing()) {
            result.setResult(false);
            result.setError("Индексация не запущена");
            SiteRecursiveTask.isInterrupted = false;
            indexRepo.deleteAllInBatch();
            lemmaRepo.deleteAllInBatch();
            pageRepo.deleteAllInBatch();
            siteRepo.deleteAllInBatch();
            siteConfig.getSites().forEach(uri -> db.saveSiteToDb(uri, Status.INDEXING));
            pool = new ForkJoinPool();
            siteRepo.findAll().stream().map(this::createIndexingThread).toList().forEach(Thread::start);
        }
        else {
            result.setResult(true);
        }
        return result;
    }

    @Override
    public Result stopSiteIndexing() {
        Result result = new Result();
        if (isIndexing()) {
            result.setResult(false);
            result.setError("Индексация уже запущена");
            SiteRecursiveTask.isInterrupted = true;
            pool.shutdownNow();
            messageType("Индексация приостановлена", true);
        }
        return result;
    }


    private void startIndexingPage(String url) {
        Thread thread = new Thread(() -> {
            Sites foundedSites = searchAvailableSites(url);
            if (foundedSites != null) convertUrlAndSaveFoundPagesToDb(url, foundedSites);
        });
        thread.start();
    }

    private Sites searchAvailableSites(String url) {
        return siteRepo.findAll().stream()
                .filter(site -> url.startsWith(site.getUrl()))
                .findFirst()
                .orElse(null);
    }

    private void convertUrlAndSaveFoundPagesToDb(String url, Sites site) {
        try {
            db.savePageToDb(Jsoup.connect(url).get(), site, url.replace(site.getUrl(), ""));
        } catch (IOException ex) {
            messageType("Проиндексация страницы данного сайта отказана: " + url, false);
        }
    }

    private Thread createIndexingThread(Sites site) {
        return new Thread(() -> {
            pool.invoke(new SiteRecursiveTask(siteRepo, pageRepo, db, site, ""));
            if (SiteRecursiveTask.isInterrupted) return;
            initAndSaveSiteToDb(site);
        });
    }

    private void initAndSaveSiteToDb(Sites site) {
        Optional<Sites> optSite = siteRepo.findSiteByUrl(site.getUrl());
        if (optSite.isPresent() && !optSite.get().getStatus().equals(Status.FAILED)) {
            optSite.get().setStatus(Status.INDEXED);
            optSite.get().setStatusTime(new Date());
            optSite.get().setLastError(null);
            siteRepo.saveAndFlush(optSite.get());
        }
    }

    @Override
    public boolean isIndexing() {
        return pool != null && (pool.getActiveThreadCount() > 0);
    }

    private void messageType(String message, boolean manuel) {
        try {
            if (pool.awaitTermination(3000, TimeUnit.MILLISECONDS)) {
                siteRepo.findAll().forEach(site -> {
                    if (manuel) site.setStatus(Status.INDEXED);
                    else site.setStatus(Status.FAILED);
                    site.setLastError(message);
                    siteRepo.saveAndFlush(site);
                });
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }
}