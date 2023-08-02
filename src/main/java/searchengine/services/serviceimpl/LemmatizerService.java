package searchengine.services.serviceimpl;

import org.apache.lucene.morphology.LuceneMorphology;
import org.apache.lucene.morphology.WrongCharaterException;
import org.apache.lucene.morphology.russian.RussianLuceneMorphology;
import org.springframework.stereotype.Service;
import searchengine.services.service.Lemmatizer;

import java.io.IOException;
import java.util.*;
import java.util.function.Consumer;

@Service
public class LemmatizerService implements Lemmatizer {

    private static final LuceneMorphology morphology;

    static {
        try {
            morphology = new RussianLuceneMorphology();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    private final Set<String> termSet = new HashSet<>(Arrays.asList("ЧАСТ", "СОЮЗ", "МЕЖД", "ПРЕДЛ"));

    @Override
    public Set<String> getNormalWords(String text) {
        Set<String> lemmas = new HashSet<>();
        processWords(splitTextIntoWords(text), w ->
                lemmas.add(normalizeWord(w)));
        return lemmas;
    }

    @Override
    public Map<String, Integer> getWordFrequencies(String text) {
        Map<String, Integer> lemmas = new HashMap<>();
        processWords(splitTextIntoWords(text), w ->
                lemmas.merge(normalizeWord(w), 1, Integer::sum));
        return lemmas;
    }

    @Override
    public Map<String, Set<String>> getGeneratedNormalWords(String text) {
        Map<String, Set<String>> lemmas = new HashMap<>();
        processWords(splitTextIntoWords(text), w ->
                lemmas.computeIfAbsent(normalizeWord(w), k -> new HashSet<>()).add(w));
        return lemmas;
    }

    private String normalizeWord(String word) throws IndexOutOfBoundsException {
        String normalForms = morphology.getNormalForms(word).get(0);
        return normalForms.length() > 0 ? normalForms : word;
    }

    public void processWords(String[] text, Consumer<String> consumer) {
        Arrays.stream(text)
                .parallel()
                .filter(w -> !isMorph(w))
                .forEachOrdered(consumer);
    }

    private String[] splitTextIntoWords(String text) throws WrongCharaterException {
        if (text == null) return new String[0];
        return text.toLowerCase(Locale.ROOT)
                .replaceAll("[^а-яё]+", " ")
                .trim()
                .split("\\s+");
    }

    private boolean isMorph(String word){
        if (word.isBlank()) return true;
        for (String morphInfo: morphology.getMorphInfo(word)) {
            if (termSet.contains(morphInfo.substring(morphInfo.indexOf(" ")))){
                return true;
            }
        }
        return false;
    }
}






