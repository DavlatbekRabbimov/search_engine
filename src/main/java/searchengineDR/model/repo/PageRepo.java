package searchengineDR.model.repo;

import searchengineDR.model.entity.Lemma;
import searchengineDR.model.entity.Page;
import searchengineDR.model.entity.Site;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface PageRepo extends JpaRepository<Page, Integer> {

    boolean existsByPathAndSite(String path, Site site);
    Integer countPageBySite(Site site);
    Optional<Page> findByPathAndSite(String path, Site site);
    @Query("SELECT p FROM Page p WHERE p.code >= 400")
    List<Page> findCodeGreaterThanOrEqual400();
    @Query(value = "SELECT * FROM pages p join indexes i on i.page_id = p. id join lemmas l on i.lemma_id = l.id where l.id in :lemmas AND p.site_id IN :sites", nativeQuery = true)
    Set<Page> findByLemmasAndSites(List<Lemma> lemmas, List<Site> sites);
    @Query(value = "SELECT * FROM pages p join indexes i on i.page_id = p. id join lemmas l on i.lemma_id = l.id where l.id = :lemma AND p.site_id IN :sites AND p.id in :pages", nativeQuery = true)
    Set<Page> findByCollections(Lemma lemma, List<Site> sites, Set<Page> pages);

}
