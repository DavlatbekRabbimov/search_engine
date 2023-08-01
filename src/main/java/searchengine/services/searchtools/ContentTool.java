package searchengine.services.searchtools;

import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.stereotype.Service;

import javax.swing.text.Document;

@Service
public class ContentTool {
    public String getProcessedContent(String content) {
        return Jsoup.clean(content, Safelist.relaxed())
                .replaceFirst("&nbsp;|<[^>]*>|\\s*\\R+\\s*|(https?://\\S+)|\\s+", " · ");
    }
}


