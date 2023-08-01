package searchengine.model.repo;

import searchengine.model.entity.Sites;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SiteRepo extends JpaRepository<Sites, Integer> {
    Optional<Sites> findSiteByUrl(String url);

}
