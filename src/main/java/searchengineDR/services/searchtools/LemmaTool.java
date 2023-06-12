package searchengineDR.services.searchtools;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import searchengineDR.model.entity.Lemma;
import searchengineDR.model.entity.Page;
import searchengineDR.model.entity.Site;
import searchengineDR.model.repo.PageRepo;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class LemmaTool {
    private final PageRepo pageRepo;
    private final SiteTool siteTool;
    public Set<Page> getFilteredLemmas(List<Lemma> lemmaList, String site) {
        List<Site> siteList = siteTool.getSiteListByUrl(site);
        List<Lemma> filteredLemmaList = lemmaList.stream().filter(lemma -> lemma.getFrequency() < 300).collect(Collectors.toList());
        Set<Page> pageSet = pageRepo.findByLemmasAndSites(filteredLemmaList, siteList);
        AtomicReference<Set<Page>> atomic = new AtomicReference<>(pageSet);
        filteredLemmaList.stream().forEach(filtered -> atomic.set(new HashSet<>(pageRepo.findByCollections(filtered, siteList, atomic.get()))));
        return atomic.get();
    }
}
