package searchengine.services.service;

import java.util.Map;
import java.util.Set;

public interface Lemmatizer {
    Set<String> getNormalWords(String text);
    Map<String, Integer> getWordFrequencies(String text);
    Map<String, Set<String>> getGeneratedNormalWords(String text);

}
