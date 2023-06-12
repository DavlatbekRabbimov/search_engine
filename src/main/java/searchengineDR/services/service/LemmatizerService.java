package searchengineDR.services.service;

import java.util.Map;
import java.util.Set;

public interface LemmatizerService {
    Set<String> getNormalWords(String text);
    Map<String, Integer> getNormalWordCounts(String text);
    Map<String, Set<String>> getGeneratedNormalWords(String text);

}
