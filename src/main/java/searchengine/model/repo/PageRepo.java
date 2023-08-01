package searchengine.model.repo;

import searchengine.model.entity.Lemmas;
import searchengine.model.entity.Pages;
import searchengine.model.entity.Sites;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface PageRepo extends JpaRepository<Pages, Integer> {

    boolean existsByPathAndSite(String path, Sites site);
    Integer countPageBySite(Sites site);
    Optional<Pages> findByPathAndSite(String path, Sites site);
    @Query(value = "SELECT * FROM pages p " +
            "JOIN indexes i ON i.page_id = p.id " +
            "JOIN lemmas l ON i.lemma_id = l.id " +
            "WHERE l.id IN :lemmas AND p.site_id IN :sites",
            nativeQuery = true)
    Set<Pages> findByLemmasAndSites(List<Lemmas> lemmas, List<Sites> sites);
    @Query(value = "SELECT * FROM pages p " +
            "JOIN indexes i ON i.page_id = p. id " +
            "JOIN lemmas l on i.lemma_id = l.id " +
            "WHERE l.id = :lemma AND p.site_id IN :sites AND p.id IN :pages",
            nativeQuery = true)
    Set<Pages> findByCollections(Lemmas lemma, List<Sites> sites, Set<Pages> pages);

}
