package searchengine.services.searchtools;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import searchengine.model.entity.Lemma;
import searchengine.services.serviceimpl.LemmatizerServiceImpl;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

@Service
@RequiredArgsConstructor
public class SnippetTool {
    private final LemmatizerServiceImpl lemmatizer;
    private final static int SNIPPET_LENGTH = 260;
    private final static int LIMIT_IN_DISTANCE_BEFORE_BOLD = 50;

    private String getSnippet(String text) {
        int startIndex = text.indexOf("<b>");
        int endIndex = text.indexOf("</b>", startIndex);

        if (startIndex >= 0 && endIndex >= 0) {
            int wordIndexInFirstBold = startIndex + "<b>".length();
            String wordInFirstBold = text.substring(wordIndexInFirstBold, endIndex).replaceAll("\\s+", " ");
            int firstBoldIndexInText = text.indexOf("<b>", text.indexOf(getBoldTexts(text, startIndex, endIndex)));
            int spaceIndex = text.indexOf(" ", Math.max(0, firstBoldIndexInText - LIMIT_IN_DISTANCE_BEFORE_BOLD));
            String nextTextPart = text.substring(spaceIndex, text.length() - 1);
            String snippetWithoutBold = nextTextPart.replaceAll("</?b>", "").trim();
            return snippetHandler(wordInFirstBold, snippetWithoutBold, nextTextPart);
        } else {
            if (text.length() > SNIPPET_LENGTH) return text.substring(0, SNIPPET_LENGTH);
            else return text;
        }
    }

    public String getBoldTexts(String text, int start, int end) {
        return Arrays.stream(text.substring(start, end).split("(?<=[.!?·])\\s*(?=(<b>|\"|«|· )?[А-ЯЁ])"))
                .filter(partText -> StringUtils.countOccurrencesOf(partText, "<b>") > 0)
                .findFirst()
                .orElse("");
    }

    public String snippetHandler(String snippet, String snippetWithoutBold, String nextTextPart) {

        int boldCount = StringUtils.countOccurrencesOf(nextTextPart, "<b>");
        int boldLength = "<b>".length() + "</b>".length();
        int snippetLengthWithBold = SNIPPET_LENGTH + boldCount + boldLength;

        if (boldCount == 0) return nextTextPart.length() > SNIPPET_LENGTH
                ? nextTextPart.substring(0, SNIPPET_LENGTH)
                : nextTextPart;

        while (boldCount >= 1) {
            int spaceIndex = nextTextPart.indexOf(" ", snippetLengthWithBold);
            if (spaceIndex != -1) snippet = nextTextPart.substring(0, spaceIndex);
            else snippet = nextTextPart;
            if (snippet.replaceAll("</?b>", "").length() < snippetWithoutBold.length()) break;
            boldCount--;
        }

        StringBuilder uniqueSnippet = new StringBuilder();
        for (String word : snippet.split("\\s+")) {
            uniqueSnippet.append(word).append(" ");
        }
        return uniqueSnippet.toString().replaceAll("\\s+", " ").trim();
    }

    private String getRussianWords(String text) {
        return text.replaceAll("([\\p{Punct}\\s\\w]+)", " ").trim();
    }

    private String tagQuery(String word, String lemma, Map<String, Set<String>> generatedWords) {
        Set<String> lemmas = generatedWords.get(lemma);
        word = getRussianWords(word);

        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < word.length(); i++) {
            char c = word.charAt(i);
            if (Character.isWhitespace(c) || Character.isLetter(c)) {
                String taggedWord = builder.append(c).toString();
                if (lemmas == null) return word.replaceAll("\\s+", " ");
                if ((lemmas.stream().anyMatch(taggedWord::equalsIgnoreCase))) {
                    word = word.replace(taggedWord, "<b>".concat(taggedWord).concat("</b>")).replaceAll("\\s+", " ");
                }
            }
        }
        return word;
    }

    public String generatedContentWithLemmaList(String text, List<Lemma> lemmaList) {
        Map<String, Set<String>> normalWords = lemmatizer.getGeneratedNormalWords(text);
        StringBuilder combinedWords = new StringBuilder();
        for (String splitText : text.split(" ")) {
            AtomicReference<String> words = new AtomicReference<>(splitText);
            lemmaList.stream().forEach(lemma -> words.set(tagQuery(words.get(), lemma.getLemma(), normalWords)));
            combinedWords.append(words).append(" ");
        }
        return getSnippet(combinedWords.toString()).replaceAll("\\s+", " ").concat(" ...").trim();
    }

}


