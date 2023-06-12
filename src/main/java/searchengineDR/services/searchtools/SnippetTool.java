package searchengineDR.services.searchtools;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import searchengineDR.model.entity.Lemma;
import searchengineDR.services.serviceimpl.LemmatizerServiceImpl;

import java.util.*;
import java.util.concurrent.atomic.AtomicReference;

@Service
@RequiredArgsConstructor
public class SnippetTool {
    private final LemmatizerServiceImpl lemmatizer;


    private String getSnippet(String text) {
        String nextTextPart = text.substring(getIndexFirstSpace(text), text.length() - 1);
        String snippetWithoutBold = nextTextPart.replaceAll("</?b>", "");
        return snippetHandler("", snippetWithoutBold, nextTextPart);
    }

    public String snippetHandler(String snippet, String snippetWithoutBold, String nextTextPart){
        int boldCount = StringUtils.countOccurrencesOf(nextTextPart, "<b>");
        while (boldCount >= 1) {
            int indexFirstSpace = nextTextPart.indexOf(" ", 250 + boldCount * 7);
            if (indexFirstSpace != -1) snippet = nextTextPart.substring(0, indexFirstSpace);
            else snippet = "";
            if (snippet.replaceAll("</?b>", "").length() < snippetWithoutBold.length()) break;
            boldCount--;
        }
        return snippet;
    }

    private int getIndexFirstSpace(String text) {
        int startIndex = text.indexOf("<b>");
        int endIndex = text.lastIndexOf("</b>");

        if (startIndex >= 0 && endIndex >= 0) {
            int boldTextCount = text.indexOf("<b>", text.indexOf(getBoldTexts(text, startIndex, endIndex)));
            int findFromThisIndex = boldTextCount > 50 ? boldTextCount - 50 : boldTextCount;
            return text.indexOf(" ", findFromThisIndex);
        } else return 0;
    }

    public String getBoldTexts(String text, int start, int end) {
        String textSeparators = "(?<=[.!?·])\\s*(?=(<b>|\"|«|· )?[А-ЯЁ])";
        return Arrays.stream(text.substring(start, end).split(textSeparators))
                .filter(partText -> StringUtils.countOccurrencesOf(partText, "<b>") > 0)
                .findFirst()
                .orElse("");
    }
    private String formatQuery(String word, String lemma, Map<String, Set<String>> generatedWords) {
        Set<String> lemmas = generatedWords.get(lemma);
        word = word.replaceAll("\\p{Punct}+", " ").replaceAll("\\w+", " ").trim();
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < word.length(); i++) {
            char c = word.charAt(i);
            if (Character.isWhitespace(c) || Character.isLetter(c)) {
                String taggedWord = builder.append(c).toString();
                if (lemmas == null) return word;
                if ((lemmas.stream().anyMatch(taggedWord.replaceAll("ё", "е")::equalsIgnoreCase))) {
                    word = word.replace(taggedWord, "<b>".concat(taggedWord).concat("</b>"));
                }
            }
        }
        return word;
    }

    public String generateSnippetWithLemmas(String text, List<Lemma> lemmaList) {
        Map<String, Set<String>> normalWords = lemmatizer.getGeneratedNormalWords(text);
        StringBuilder builderText = new StringBuilder();
        for (String word : text.split(" ")) {
            AtomicReference<String> formatWord = new AtomicReference<>(word);
            lemmaList.stream().forEach(lemma
                    -> formatWord.set(formatQuery(formatWord.get(), lemma.getLemma(), normalWords)));
            builderText.append(formatWord).append(" ");
        }
        return getSnippet(builderText.toString()).concat(" ...");
    }

}



