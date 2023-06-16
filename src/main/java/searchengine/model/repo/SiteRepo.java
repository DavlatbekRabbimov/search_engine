package searchengine.model.repo;

import searchengine.model.entity.Site;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SiteRepo extends JpaRepository<Site, Long> {
    Optional<Site> findSiteByUrl(String url);

}
