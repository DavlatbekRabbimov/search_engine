package searchengine.services.serviceimpl;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.Setter;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Safelist;
import org.springframework.stereotype.Service;
import searchengine.config.Uri;
import searchengine.model.*;
import searchengine.model.entity.Indexes;
import searchengine.model.entity.Lemmas;
import searchengine.model.entity.Pages;
import searchengine.model.entity.Sites;
import searchengine.model.repo.IndexRepo;
import searchengine.model.repo.LemmaRepo;
import searchengine.model.repo.PageRepo;
import searchengine.model.repo.SiteRepo;
import searchengine.services.parser.SiteRecursiveTask;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@AllArgsConstructor
@Getter
@Setter
@Service
public class DbService {
    private final SiteRepo siteRepo;
    private final PageRepo pageRepo;
    private final LemmaRepo lemmaRepo;
    private final IndexRepo indexRepo;
    private final LemmatizerService lemmatizer;
    private static final Integer BAD_REQUEST = 400;

    public void saveSiteToDb(Uri uri, Status status) {
        String processedUrl = StringUtils.stripEnd(uri.getUrl(), "/");
        siteRepo.saveAndFlush(new Sites(status, new Date(), "", processedUrl, uri.getName()));
    }

    public void savePageToDb(Document document, Sites site, String path) {
        String processedPath = StringUtils.defaultIfBlank(path, "/");
        int statusCode = document.connection().response().statusCode();

        siteRepo.findSiteByUrl(site.getUrl()).ifPresent(siteEntity -> {
            siteEntity.setLastError(null);
            siteEntity.setStatusTime(new Date());
            siteRepo.saveAndFlush(siteEntity);
        });

        Pages page = pageRepo.findByPathAndSite(processedPath, site).orElseGet(()
                -> new Pages(site, processedPath, statusCode, document.html()));
        pageRepo.saveAndFlush(page);

        if (page.getCode() < BAD_REQUEST) {
            saveLemmaToDb(page);
            saveIndexToDb(page);
        }
    }

    private void saveLemmaToDb(Pages page) {
        Set<Lemmas> lemmas = ConcurrentHashMap.newKeySet();
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

    private void saveIndexToDb(Pages page) {
        Set<Indexes> indexes = ConcurrentHashMap.newKeySet();
        synchronized (this) {
            getNormalizedWordCounts(page).forEach((lemma, rank) -> {
                if (SiteRecursiveTask.isInterrupted) return;
                indexes.add(new Indexes(page, lemmaSaverSetting(page, lemma), rank));
            });
            indexRepo.saveAllAndFlush(indexes);
        }
    }

    private Map<String, Integer> getNormalizedWordCounts(Pages page) {
        return lemmatizer.getWordFrequencies(Jsoup.clean(page.getContent(), Safelist.relaxed()).replaceAll("ё", "е"));
    }

    private Lemmas lemmaSaverSetting(Pages page, String lemmaName) {
        return lemmaRepo.findByLemma(lemmaName)
                .map(lemma -> {
                    lemma.setFrequency(lemma.getFrequency() + 1);
                    return lemma;
                }).orElse(new Lemmas(page.getSite(), lemmaName, 1));
    }
}
