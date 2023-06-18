package searchengine.services.parser;

import lombok.AllArgsConstructor;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import searchengine.model.*;

import searchengine.model.entity.Site;
import searchengine.model.repo.PageRepo;
import searchengine.model.repo.SiteRepo;
import searchengine.services.serviceimpl.DbService;

import java.io.IOException;
import java.util.*;
import java.util.concurrent.*;
import java.util.stream.Collectors;

@AllArgsConstructor
public class SiteRecursiveTask extends RecursiveAction {

    private final SiteRepo siteRepo;
    private final PageRepo pageRepo;
    private final DbService db;
    private final Site site;
    private final String path;
    public static volatile boolean isInterrupted = true;
    private static final int WAIT_TIME_MS = 300;
    private static final int CONNECTION_TIMEOUT_MS = 10_000;
    private static final String USER_AGENT = "Mozilla/5.0 (Windows; U; WindowsNT 5.1; en-US; rv1.8.1.6) Gecko/20070725 Firefox/2.0.0.6";
    private static final String REFERRER = "https://www.google.com/";

    @Override
    protected void compute() {
        try {
            if (isInterrupted && pageRepo.existsByPathAndSite(path, site)) return;
            db.savePageToDb(getDocument(), site, path);
            invokeAll(getAbsUrl(getDocument()).stream()
                    .map(this::getRecursiveByUrl)
                    .collect(Collectors.toSet()));
        } catch (CancellationException ignore) {
        } catch (Exception e) {
            siteRepo.findSiteByUrl(site.getUrl()).ifPresent(site -> {
                        site.setStatus(Status.FAILED);
                        site.setLastError("Тип ошибки: " + e.getMessage());
                        siteRepo.saveAndFlush(site);
                    });
        }
    }
    private SiteRecursiveTask getRecursiveByUrl(String url) {
        String processedUrl = url.equals(site.getUrl()) ? "/" : url.substring(site.getUrl().length());
        return new SiteRecursiveTask(siteRepo, pageRepo, db, site, processedUrl);
    }
    private Document getDocument() {
        try {
            Thread.sleep(WAIT_TIME_MS);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
        try {
            return Jsoup.connect(site.getUrl().concat(path))
                    .userAgent(USER_AGENT)
                    .referrer(REFERRER)
                    .ignoreHttpErrors(true)
                    .ignoreContentType(true)
                    .followRedirects(false)
                    .timeout(CONNECTION_TIMEOUT_MS)
                    .get();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    private Set<String> getAbsUrl(Document document) {
        return document.select("a[href]")
                .stream()
                .map(attribute -> attribute.absUrl("href"))
                .filter(this::isCorrectUrl)
                .collect(Collectors.toSet());
    }

    private boolean isCorrectUrl(String url) {
        return !url.isEmpty() &&
                url.startsWith(site.getUrl()) &&
                !url.contains("#") &&
                !url.matches("([\\w\\W]+(\\.(?i)(jpg|jpe?g|png|gif|bmp|pdf|doc|docx|DOC|PNG|JPE?G|JPG|php[\\W\\w]|#[\\w\\W]*|\\?[\\w\\W]+)))$");
    }
}


