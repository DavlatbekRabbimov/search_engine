package searchengineDR.services.searchtools;

import org.jsoup.Jsoup;
import org.springframework.stereotype.Service;

@Service
public class TitleTool {
    public String getTitle(String content) {
        return Jsoup.parse(content).title();
    }
}