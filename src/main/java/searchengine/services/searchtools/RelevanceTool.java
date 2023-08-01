package searchengine.services.searchtools;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import searchengine.model.entity.Indexes;
import searchengine.model.repo.IndexRepo;

@Service
public class RelevanceTool {

    private final IndexRepo indexRepo;
    private float cachedMaxRank;

    @Autowired
    public RelevanceTool(IndexRepo indexRepo) {
        this.indexRepo = indexRepo;
        updateMaxRank();
    }

    @Scheduled(fixedRate = 1000 * 60 * 60 * 24)
    private void updateMaxRank() {
        cachedMaxRank = indexRepo.findAll()
                .stream()
                .map(Indexes::getRank)
                .max(Float::compare)
                .orElse(0F);
    }

    private float getMaxRank() {
        return cachedMaxRank;
    }

    public float getPageRank(int pageId) {
        float maxRank = getMaxRank();
        float pageRankSum = indexRepo.findAllByPageId(pageId)
                .stream()
                .map(Indexes::getRank)
                .reduce(0F, Float::sum);

        return pageRankSum / maxRank;
    }
}


