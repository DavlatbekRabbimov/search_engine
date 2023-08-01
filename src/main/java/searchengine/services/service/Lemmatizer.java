package searchengine.services.service;

import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public interface Lemmatizer {
    Set<String> getNormalWords(String text);
    Map<String, Integer> getWordFrequencies(String text);
    Map<String, HashSet<String>> getGeneratedNormalWords(String text);

}
