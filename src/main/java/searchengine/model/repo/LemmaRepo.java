package searchengine.model.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import searchengine.model.entity.Lemmas;
import searchengine.model.entity.Sites;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Repository
public interface LemmaRepo extends JpaRepository<Lemmas, Integer> {
    Optional<Lemmas> findByLemma(String lemmaName);
    Integer countLemmaBySite(Sites site);
    @Query(value = "SELECT * FROM lemmas l " +
            "WHERE l.`lemma` IN :lemmaSet " +
            "ORDER BY l.`frequency`",
            nativeQuery = true)
    Optional<List<Lemmas>> findByLemmaSet(Set<String> lemmaSet);
}
