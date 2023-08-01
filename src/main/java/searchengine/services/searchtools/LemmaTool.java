package searchengine.services.searchtools;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import searchengine.model.entity.Lemmas;
import searchengine.model.entity.Pages;
import searchengine.model.entity.Sites;
import searchengine.model.repo.PageRepo;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;
import java.util.stream.Collectors;

@AllArgsConstructor
@Service
public class LemmaTool {
    private final PageRepo pageRepo;
    private final SiteTool siteTool;
    public Set<Pages> getFilteredLemmas(List<Lemmas> lemmaList, String site) {
        List<Sites> siteList = siteTool.getSiteByUrl(site);
        List<Lemmas> filteredLemmaList = lemmaList.stream().filter(lemma -> lemma.getFrequency() < 300).collect(Collectors.toList());
        Set<Pages> pageSet = pageRepo.findByLemmasAndSites(filteredLemmaList, siteList);
        AtomicReference<Set<Pages>> atomic = new AtomicReference<>(pageSet);
        filteredLemmaList.stream().forEach(filteredLemmas -> atomic.set(new HashSet<>(pageRepo.findByCollections(filteredLemmas, siteList, atomic.get()))));
        return atomic.get();
    }
}
