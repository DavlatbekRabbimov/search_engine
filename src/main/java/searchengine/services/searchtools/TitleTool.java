package searchengine.services.searchtools;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

@Service
public class TitleTool {

    private static final String DEFAULT_TITLE = "При индексации данного сайта, необнаружен заголовок";
    public String getTitle(String content) {
        Element titleElement = Jsoup.parse(content).selectFirst("title");
        if (titleElement != null) return titleElement.text();
        else return DEFAULT_TITLE;
    }
}