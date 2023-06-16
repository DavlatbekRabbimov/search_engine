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

    private String getSnippet(String text) {
        int startIndex = text.indexOf("<b>");
        int endIndex = text.indexOf("</b>", startIndex);

        if (startIndex >= 0 && endIndex >= 0) {
            startIndex += "<b>".length();
            String snippet = text.substring(startIndex, endIndex).replaceAll("\\s+", " ");
            String nextTextPart = text.substring(getIndexFirstSpace(text, endIndex), text.length() - 1);
            String snippetWithoutBold = nextTextPart.replaceAll("</?b>", "");
            return snippetHandler(snippet, snippetWithoutBold, nextTextPart);
        } else {
            if (text.length() > 260) return text.substring(0, 260);
            else return text;
        }
    }

    public String snippetHandler(String snippet, String snippetWithoutBold, String nextTextPart) {
        int boldCount = StringUtils.countOccurrencesOf(nextTextPart, "<b>");

        if (boldCount == 0) return nextTextPart.length() > 260 ? nextTextPart.substring(0, 260) : nextTextPart;

        while (boldCount >= 1) {
            int indexFirstSpace = nextTextPart.indexOf(" ", 260 + boldCount * 7);
            if (indexFirstSpace != -1) snippet = nextTextPart.substring(0, indexFirstSpace);
            else snippet = nextTextPart;
            if (snippet.replaceAll("</?b>", "").length() < snippetWithoutBold.length()) break;
            boldCount--;
        }

        String[] words = snippet.split("\\s+");
        StringBuilder uniqueSnippet = new StringBuilder();
        Set<String> uniqueWords = new HashSet<>();

        for (String word : words) {
            if (uniqueWords.add(word)) uniqueSnippet.append(word).append(" ");
        }
        return uniqueSnippet.toString().replaceAll("\\s+", " ").trim();
    }

    private int getIndexFirstSpace(String text, int endIndex) {
        int startIndex = text.indexOf("<b>");
        if (startIndex >= 0 && endIndex >= 0) {
            int boldTextCount = text.indexOf("<b>", text.indexOf(getBoldTexts(text, startIndex, endIndex)));
            int findFromThisIndex = Math.max(0, boldTextCount - 50);
            return text.indexOf(" ", findFromThisIndex);
        }
        return 0;
    }


    public String getBoldTexts(String text, int start, int end) {
        return Arrays.stream(text.substring(start, end).split("(?<=[.!?·])\\s*(?=(<b>|\"|«|· )?[А-ЯЁ])"))
                .filter(partText -> StringUtils.countOccurrencesOf(partText, "<b>") > 0)
                .findFirst()
                .orElse("");
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


