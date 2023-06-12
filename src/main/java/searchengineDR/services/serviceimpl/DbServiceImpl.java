package searchengineDR.services.serviceimpl;


import lombok.Getter;
import lombok.RequiredArgsConstructor;
import lombok.Setter;

import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.safety.Safelist;
import org.springframework.stereotype.Service;
import searchengineDR.config.Uri;
import searchengineDR.model.*;
import searchengineDR.model.entity.Index;
import searchengineDR.model.entity.Lemma;
import searchengineDR.model.entity.Page;
import searchengineDR.model.entity.Site;
import searchengineDR.model.repo.IndexRepo;
import searchengineDR.model.repo.LemmaRepo;
import searchengineDR.model.repo.PageRepo;
import searchengineDR.model.repo.SiteRepo;
import searchengineDR.parser.SiteRecursiveTask;
import searchengineDR.services.service.DbService;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

@RequiredArgsConstructor
@Getter
@Setter
@Service
public class DbServiceImpl implements DbService {
    private final SiteRepo siteRepo;
    private final PageRepo pageRepo;
    private final LemmaRepo lemmaRepo;
    private final IndexRepo indexRepo;
    private final LemmatizerServiceImpl lemmatizer;
    private static final Integer BAD_REQUEST = 400;

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

    @Override
    public Map<String, Integer> getNormalizedWordCounts(Page page) {
        return lemmatizer.getNormalWordCounts(Jsoup.clean(page.getContent(), Safelist.relaxed()).replaceAll("ё", "е"));
    }

    @Override
    public Lemma lemmaSaverSetting(Page page, String lemmaName) {
        return lemmaRepo.findOptByLemma(lemmaName).map(lemma -> {
            lemma.setFrequency(lemma.getFrequency() + 1);
            return lemma;
        }).orElse(new Lemma(page.getSite(), lemmaName, 1));
    }
}
