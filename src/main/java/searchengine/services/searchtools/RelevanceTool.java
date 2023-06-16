package searchengine.services.searchtools;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import searchengine.model.entity.Page;
import searchengine.model.repo.IndexRepo;

import javax.transaction.Transactional;

@Service
public class RelevanceTool {

    private final IndexRepo indexRepo;

    @Autowired
    public RelevanceTool(IndexRepo indexRepo) {
        this.indexRepo = indexRepo;
    }

    @Transactional
    public float getRelevance(Page page) {
        return indexRepo.getRelevance(page, indexRepo.findMaxSum());
    }

}
