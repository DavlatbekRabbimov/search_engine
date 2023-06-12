package searchengineDR.services.searchtools;

import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import searchengineDR.config.SiteConfig;
import searchengineDR.model.entity.Site;
import searchengineDR.model.repo.SiteRepo;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

@AllArgsConstructor
@Service
public class SiteTool {

    private final SiteConfig siteConfig;
    private final SiteRepo siteRepo;

    public List<Site> getSiteListByUrl(String url) {
        return Optional.ofNullable(url)
                .flatMap(siteRepo::findSiteByUrl)
                .map(Collections::singletonList)
                .orElseGet(siteRepo::findAll);
    }
    public boolean isUrlOrPageFound(String url) {
        return siteConfig.getSites()
                .stream()
                .anyMatch(s -> s.getUrl().replaceAll("/$", "").equals(url));
    }

    public boolean isUrlFound(String url) {

        if (!url.matches("https?://.*") && isUrlNotFound(url)) {
            return false;
        }
        return siteConfig.getSites()
                .stream()
                .anyMatch(uri -> url.startsWith(uri.getUrl()));
    }

    public boolean isUrlNotFound(String url) {
        try {
            URLConnection connection = new URL(url).openConnection();
            connection.connect();
            int statusCode = ((HttpURLConnection) connection).getResponseCode();
            return statusCode == 404;
        } catch (IOException e) {
            return false;
        }
    }
}
