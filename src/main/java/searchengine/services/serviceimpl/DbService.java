package searchengine.services.serviceimpl;

import lombok.Getter;
import lombok.Setter;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Safelist;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import searchengine.config.Uri;
import searchengine.model.*;
import searchengine.model.entity.Index;
import searchengine.model.entity.Lemma;
import searchengine.model.entity.Page;
import searchengine.model.entity.Site;
import searchengine.model.repo.IndexRepo;
import searchengine.model.repo.LemmaRepo;
import searchengine.model.repo.PageRepo;
import searchengine.model.repo.SiteRepo;
import searchengine.services.parser.SiteRecursiveTask;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@Getter
@Setter
@Service
public class DbService {
    private final SiteRepo siteRepo;
    private final PageRepo pageRepo;
    private final LemmaRepo lemmaRepo;
    private final IndexRepo indexRepo;
    private final LemmatizerServiceImpl lemmatizer;
    private static final Integer BAD_REQUEST = 400;

    @Autowired
    public DbService(SiteRepo siteRepo, PageRepo pageRepo, LemmaRepo lemmaRepo, IndexRepo indexRepo, LemmatizerServiceImpl lemmatizer) {
        this.siteRepo = siteRepo;
        this.pageRepo = pageRepo;
        this.lemmaRepo = lemmaRepo;
        this.indexRepo = indexRepo;
        this.lemmatizer = lemmatizer;
    }

    public void saveSiteToDb(Uri uri, Status status) {
        String processedUrl = StringUtils.stripEnd(uri.getUrl(), "/");
        siteRepo.saveAndFlush(new Site(status, new Date(), "", processedUrl, uri.getName()));
    }

    public void savePageToDb(Document document, Site site, String path) {
        String processedPath = StringUtils.defaultIfBlank(path, "/");
        int statusCode = document.connection().response().statusCode();

        siteRepo.findSiteByUrl(site.getUrl()).ifPresent(siteEntity -> {
            siteEntity.setLastError(null);
            siteEntity.setStatusTime(new Date());
            siteRepo.saveAndFlush(siteEntity);
        });

        Page page = pageRepo.findByPathAndSite(processedPath, site).orElseGet(()
                -> new Page(site, processedPath, statusCode, document.html()));
        pageRepo.saveAndFlush(page);

        if (page.getCode() < BAD_REQUEST) {
            saveLemmaToDb(page);
            saveIndexToDb(page);
        }
    }

    private void saveLemmaToDb(Page page) {
        Set<Lemma> lemmas = ConcurrentHashMap.newKeySet();
        synchronized (this) {
            getNormalizedWordCounts(page)
                    .entrySet()
                    .stream()
                    .filter(entry -> !SiteRecursiveTask.isInterrupted)
                    .map(entry -> lemmaSaverSetting(page, entry.getKey()))
                    .forEach(lemmas::add);
            lemmaRepo.saveAllAndFlush(lemmas);
        }
    }

    private void saveIndexToDb(Page page) {
        Set<Index> indexes = ConcurrentHashMap.newKeySet();
        synchronized (this) {
            getNormalizedWordCounts(page).forEach((lemma, rank) -> {
                if (SiteRecursiveTask.isInterrupted) return;
                indexes.add(new Index(page, lemmaSaverSetting(page, lemma), rank));
            });
            indexRepo.saveAllAndFlush(indexes);
        }
    }

    private Map<String, Integer> getNormalizedWordCounts(Page page) {
        return lemmatizer.getNormalWordCounts(Jsoup.clean(page.getContent(), Safelist.relaxed()).replaceAll("ё", "е"));
    }

    private Lemma lemmaSaverSetting(Page page, String lemmaName) {
        return lemmaRepo.findByLemma(lemmaName).map(lemma -> {
            lemma.setFrequency(lemma.getFrequency() + 1);
            return lemma;
        }).orElse(new Lemma(page.getSite(), lemmaName, 1));
    }
}
