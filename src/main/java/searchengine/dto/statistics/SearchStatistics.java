package searchengine.dto.statistics;
import lombok.*;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class SearchStatistics implements Comparable<SearchStatistics>{
    private String site;
    private String siteName;
    private String uri;
    private String title;
    private String snippet;
    private float relevance;
    @Override
    public int compareTo(SearchStatistics o) {
        return Float.compare(getRelevance(), o.getRelevance());
    }
}
