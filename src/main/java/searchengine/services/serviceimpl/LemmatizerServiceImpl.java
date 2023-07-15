package searchengine.services.serviceimpl;
import org.apache.lucene.morphology.LuceneMorphology;
import org.apache.lucene.morphology.WrongCharaterException;
import org.apache.lucene.morphology.russian.RussianLuceneMorphology;
import org.springframework.stereotype.Service;
import searchengine.services.service.LemmatizerService;

import java.io.IOException;
import java.util.*;

@Service
public class LemmatizerServiceImpl implements LemmatizerService {

    private final LuceneMorphology morphology;
    private static final Set<String> termSet = new TreeSet<>(Arrays.asList("ЧАСТ", "СОЮЗ", "МЕЖД", "ПРЕДЛ"));

    public LemmatizerServiceImpl() throws IOException{
        this.morphology = new RussianLuceneMorphology();
    }

    @Override
    public Set<String> getNormalWords(String text) {
        HashSet<String> lemmas = new HashSet<>();
        for (String word : getProcessedWords(text)) {
            if (isMorph(word) || morphology.getNormalForms(word).isEmpty()) continue;
            lemmas.add(morphology.getNormalForms(word).get(0));
        }
        return lemmas;
    }

    @Override
    public Map<String, Integer> getNormalWordCounts(String text) {
        HashMap<String, Integer> lemmas = new HashMap<>();
        for (String word : getProcessedWords(text)) {
            if (isMorph(word) || morphology.getNormalForms(word).isEmpty()) continue;
            lemmas.merge(morphology.getNormalForms(word).get(0), 1, Integer::sum);
        }
        return lemmas;
    }

    @Override
    public Map<String, Set<String>> getGeneratedNormalWords(String text) {
        HashMap<String, Set<String>> lemmas = new HashMap<>();
        for (String word : getProcessedWords(text)) {
            if (isMorph(word) || morphology.getNormalForms(word).isEmpty()) continue;
            lemmas.computeIfAbsent(morphology.getNormalForms(word).get(0), key -> new HashSet<>()).add(word);
        }
        return lemmas;
    }

    private boolean isMorph(String text) throws WrongCharaterException {
        if (text.isBlank()) return true;
        for (String morphWord : morphology.getMorphInfo(text)) {
            if (termSet.contains(morphWord.substring(morphWord.indexOf(" ")))) return true;
        }
        return false;
    }

    private String[] getProcessedWords(String text) throws WrongCharaterException {
        if (text == null) return new String[0];
        return text.toLowerCase(Locale.ROOT)
                .replaceAll("[^а-яё]+", " ")
                .trim()
                .split("\\s+");
    }
}
