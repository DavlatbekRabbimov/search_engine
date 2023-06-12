package searchengineDR.services.service;

import searchengineDR.model.entity.Lemma;
import searchengineDR.model.entity.Page;

import java.util.Map;

public interface DbService {
    Map<String, Integer> getNormalizedWordCounts(Page page);
    Lemma lemmaSaverSetting(Page page, String lemmaName);
}