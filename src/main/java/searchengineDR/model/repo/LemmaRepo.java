package searchengineDR.model.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import searchengineDR.model.entity.Lemma;
import searchengineDR.model.entity.Site;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface LemmaRepo extends JpaRepository<Lemma, Long> {
    Optional<Lemma> findOptByLemma(String lemma);
    Integer countLemmaBySite(Site site);
    @Query(value = "SELECT * FROM lemmas WHERE `lemma` IN :lemmaSet ORDER BY `frequency` ASC", nativeQuery = true)
    Optional<List<Lemma>> findByLemmaSet(Set<String> lemmaSet);
}
