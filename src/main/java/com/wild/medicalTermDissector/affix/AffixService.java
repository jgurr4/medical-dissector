package com.wild.medicalTermDissector.affix;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Service
public class AffixService {

  private final AffixRepository affixRepository;

  @Autowired
  public AffixService(AffixRepository affixRepository) {
    this.affixRepository = affixRepository;
  }

  public Map<String, List<Affix>> dissect(String term) {
    List<Affix> affixes;
    List<Affix> matches;
    Map<String, List<Affix>> dissectedParts = new HashMap<>();
    String subterm = "";
    String originalTerm = term;
    for (int i = 1; i <= term.length(); i++) {
      if (term.length() == 1) {
        return dissectedParts;
      }
      affixes = affixRepository.findByAffixStartsWith(term.substring(0, i), true);
      if (affixes.size() == 1) {
        String[] variations = findVariations(affixes.get(0));
        if (variations.length > 1) {
          for (int j = 0; j < variations.length; j++) {
            if (variations[j] == term.substring(0, i)) {
              subterm = term.substring(0, i);
              dissectedParts.put(subterm, affixes);
              term = term.substring(i);
              break;
            } else {
              subterm = variations[0];
              dissectedParts.put(subterm, affixes);
              term = term.replace(subterm, "");
              i = 0;
            }
          }
        } else {
          subterm = variations[0];
          dissectedParts.put(subterm, affixes);
          term = term.replace(subterm, "");
          i = 0;
        }
      } else if (i == term.length()) {
        matches = findMatches(affixes, term);
        if (matches.isEmpty()) {
          dissectedParts.put(subterm, affixes);   // Add all related records for the affix and let the user choose.
        } else {
          dissectedParts.put(subterm, matches); // Add all matching records for the affix and let the user choose if more than one exists.
        }
      }
    }
    return dissectedParts;
  }

  private List<Affix> findMatches(List<Affix> affixes, String term) {
    List<Affix> correctAffix = new ArrayList<>();
    for (int i = 0; i < affixes.size(); i++) {
      String[] variations = findVariations(affixes.get(i));
      for (int j = 0; j < variations.length; j++) {
        if (variations[j].equals(term)) {
          correctAffix.add(affixes.get(i));
        }
      }
    }
    return correctAffix;
  }

  public String[] findVariations(Affix affix) {
    return affix.getAffix()
      .replaceAll("\\[[0-9]\\]", "")
      .replace("(", "")
      .replace(")", "")
      .replace("-", "")
      .split(",");  // At this point -algia, alg(i)o- would become { algia, algio } so now it's easier to compare to term.substring(0, i)
  }

}
