package searchengineDR.services.searchtools;

import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.stereotype.Service;

@Service
public class ContentTool {
    public String getProcessedContent(String content) {
        return Jsoup.clean(content, Safelist.relaxed())
                .replaceAll("&nbsp;|<[^>]*>|https?://\\S+|\\s*\\R+\\s*", " ")
                .replaceAll("\\s+", " · ");
    }
}
