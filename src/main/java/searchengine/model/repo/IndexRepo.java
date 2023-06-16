package searchengine.model.repo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import searchengine.model.entity.Index;
import searchengine.model.entity.Page;

@Repository
public interface IndexRepo extends JpaRepository<Index, Long> {
    @Query(value = "SELECT MAX(sum) FROM (SELECT SUM(`rank`) sum FROM `indexes` i GROUP BY `page_id`) AS `value`", nativeQuery = true)
    Float findMaxSum();
    @Query(value = "SELECT SUM(`rank`) / :maxValue FROM `indexes` i WHERE  page_id = :page", nativeQuery = true)
    Float getRelevance(Page page, @Param("maxValue") float maxValue);
}