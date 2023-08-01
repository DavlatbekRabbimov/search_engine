package searchengine.model.repo;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import searchengine.model.entity.Indexes;

import java.util.List;

@Repository
public interface IndexRepo extends JpaRepository<Indexes, Integer> {
    List<Indexes> findAllByPageId(int pageId);
}