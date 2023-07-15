package searchengine.services.searchtools;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import searchengine.model.entity.Lemma;
import searchengine.model.entity.Page;
import searchengine.model.entity.Site;
import searchengine.model.repo.PageRepo;

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
        filteredLemmaList.stream().forEach(filteredLemmas -> atomic.set(new HashSet<>(pageRepo.findByCollections(filteredLemmas, siteList, atomic.get()))));
        return atomic.get();
    }
}
