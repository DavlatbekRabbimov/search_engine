package FunctionalTest;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.MockitoAnnotations;
import org.mockito.junit.jupiter.MockitoExtension;
import searchengine.services.serviceimpl.LemmatizerServiceImpl;

import java.io.IOException;
import java.util.*;

import static org.junit.jupiter.api.Assertions.assertEquals;

@ExtendWith(MockitoExtension.class)
public class LemmatizerServiceImplTest {
    LemmatizerServiceImpl lemmatizerService;
    @BeforeEach
    public void setUp() throws IOException {

        MockitoAnnotations.openMocks(this);
        this.lemmatizerService = new LemmatizerServiceImpl();

    }

    @Test
    public void testGetNormalWords() {

        String text = "Вижу много домов в городе";

        HashSet<String> expected = new LinkedHashSet<>(List.of("видеть", "много", "дом", "в", "город"));
        Set<String> actual = lemmatizerService.getNormalWords(text);

        assertEquals(expected, actual);
    }

    @Test
    public void testGetNormalWordCounts() {
        String text = "Вижу много домов в городе или в городах";

        Map<String, Integer> expected = new HashMap<>();
        List<String> wordList = List.of("видеть", "много", "дом", "в", "город", "или", "в", "город");
        for (String words: wordList) {
            expected.merge(words, 1, Integer::sum);
        }

        Map<String, Integer> actual = lemmatizerService.getNormalWordCounts(text);

        assertEquals(expected, actual);
    }

    @Test
    public void testGetGeneratedNormalWords() {
        String text = "Вижу много домов в городе или в городах";

        Map<String, Set<String>> expected = new HashMap<>();
        List<String> wordList = List.of("вижу", "много", "домов", "в", "городе", "или", "в", "городах");
        for (String words: wordList){
            String normalWords = String.join(" ", lemmatizerService.getNormalWords(words));
            expected.computeIfAbsent(normalWords, key -> new HashSet<>()).add(words);
        }

        Map<String, Set<String>> actual = lemmatizerService.getGeneratedNormalWords(text);

        assertEquals(expected, actual);
    }
}
