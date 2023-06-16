package searchengine.services.service;

import searchengine.model.entity.Lemma;
import searchengine.model.entity.Page;

import java.util.Map;

public interface DbService {
    Map<String, Integer> getNormalizedWordCounts(Page page);
    Lemma lemmaSaverSetting(Page page, String lemmaName);
}