package searchengineDR.services.serviceimpl;

import org.apache.commons.lang3.StringUtils;
import searchengineDR.config.SiteConfig;
import searchengineDR.config.Uri;
import searchengineDR.dto.statistics.DetailedStatisticsItem;
import searchengineDR.dto.statistics.StatisticsData;
import searchengineDR.dto.statistics.StatisticsResponse;
import searchengineDR.dto.statistics.TotalStatistics;
import searchengineDR.model.Status;
import searchengineDR.model.entity.Site;
import searchengineDR.model.repo.LemmaRepo;
import searchengineDR.model.repo.PageRepo;
import searchengineDR.model.repo.SiteRepo;
import searchengineDR.services.service.IndexingService;
import searchengineDR.services.service.StatisticsService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
@RequiredArgsConstructor
public class StatisticsServiceImpl implements StatisticsService {

    private final SiteRepo siteRepo;
    private final PageRepo pageRepo;
    private final LemmaRepo lemmaRepo;
    private final IndexingService indexingService;
    private final DbServiceImpl db;
    private final SiteConfig siteConfig;

    @Override
    public StatisticsResponse getStatistics() {
        TotalStatistics total = new TotalStatistics();
        total.setSites(siteConfig.getSites().size());
        total.setIndexing(indexingService.isIndexing());

        List<DetailedStatisticsItem> detailed = new ArrayList<>();
        for (Uri siteUri : siteConfig.getSites()) {
            String url = StringUtils.chomp(siteUri.getUrl(), "/");
            if (siteRepo.findSiteByUrl(url).isEmpty()) db.saveSiteToDb(siteUri, Status.INDEXED);
            siteRepo.findSiteByUrl(url).ifPresent(site -> detailed.add(initItem(site, total)));
        }
        return new StatisticsResponse(true, new StatisticsData(total, detailed));
    }
    private DetailedStatisticsItem initItem(Site site, TotalStatistics total) {
        String status = site.getStatus().toString();
        String error = site.getLastError();
        long statusTime = site.getStatusTime().getTime();
        int pages = pageRepo.countPageBySite(site);
        int lemmas = lemmaRepo.countLemmaBySite(site);
        total.setPages(total.getPages() + pages);
        total.setLemmas(total.getLemmas() + lemmas);
        return new DetailedStatisticsItem(site.getUrl(), site.getName(), status, statusTime, error, pages, lemmas);
    }
}