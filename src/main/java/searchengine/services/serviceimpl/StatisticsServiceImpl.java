package searchengine.services.serviceimpl;

import lombok.RequiredArgsConstructor;
import org.apache.commons.lang3.StringUtils;
import org.springframework.stereotype.Service;
import searchengine.config.SiteConfig;
import searchengine.config.Uri;
import searchengine.dto.statistics.DetailedStatisticsItem;
import searchengine.dto.statistics.StatisticsData;
import searchengine.dto.statistics.StatisticsResponse;
import searchengine.dto.statistics.TotalStatistics;
import searchengine.model.Status;
import searchengine.model.entity.Site;
import searchengine.model.repo.LemmaRepo;
import searchengine.model.repo.PageRepo;
import searchengine.model.repo.SiteRepo;
import searchengine.services.service.IndexingService;
import searchengine.services.service.StatisticsService;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {

    private final SiteRepo siteRepo;
    private final PageRepo pageRepo;
    private final LemmaRepo lemmaRepo;
    private final IndexingService indexingService;
    private final DbService db;
    private final SiteConfig siteConfig;

    @Override
    public StatisticsResponse getStatistics() {
        TotalStatistics total = new TotalStatistics();
        total.setSites(siteConfig.getSites().size());
        total.setIndexing(indexingService.isIndexing());

        List<DetailedStatisticsItem> detailed = new ArrayList<>();
        for (Uri siteUri : siteConfig.getSites()) {
            String siteUrl = StringUtils.substringBeforeLast(siteUri.getUrl(), "/");
            if (siteRepo.findSiteByUrl(siteUrl).isEmpty()) db.saveSiteToDb(siteUri, Status.INDEXED);
            siteRepo.findSiteByUrl(siteUrl).ifPresent(site -> detailed.add(initItem(site, total)));
        }
        return new StatisticsResponse(true, new StatisticsData(total, detailed));
    }
    private DetailedStatisticsItem initItem(Site site, TotalStatistics total) {
        String siteStatus = site.getStatus().toString();
        String errorResult = site.getLastError();
        long time = site.getStatusTime().getTime();
        int pages = pageRepo.countPageBySite(site);
        int lemmas = lemmaRepo.countLemmaBySite(site);
        total.setPages(total.getPages() + pages);
        total.setLemmas(total.getLemmas() + lemmas);
        return new DetailedStatisticsItem(site.getUrl(), site.getName(), siteStatus, time, errorResult, pages, lemmas);
    }
}