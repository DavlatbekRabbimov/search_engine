package searchengine.services.searchtools;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import searchengine.model.entity.Page;
import searchengine.model.repo.IndexRepo;

@Service
@AllArgsConstructor
public class RelevanceTool {
    private final IndexRepo indexRepo;
    public float getRelevance(Page page) {
        return indexRepo.getRelevance(page, indexRepo.findMaxSum());
    }

}
