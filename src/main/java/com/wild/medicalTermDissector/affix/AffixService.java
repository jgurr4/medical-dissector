package com.wild.medicalTermDissector.affix;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

@Service
public class AffixService {

  private final AffixRepository affixRepository;

  @Autowired
  public AffixService(AffixRepository affixRepository) {
    this.affixRepository = affixRepository;
  }

  public List<Affix> dissect(String term) {
    List<Affix> affixes;
    Affix correctAffix;
    List<Affix> dissectedParts = new ArrayList<>();
    for (int i = 1; i <= term.length(); i++) {
      if (term.length() == 1) {
        return dissectedParts;
      }
      affixes = affixRepository.findByAffixStartsWith(term.substring(0, i));
      if (affixes.size() == 1) {
        dissectedParts.add(affixes.get(0));
        String[] variations = findVariations(affixes.get(0));
        if (variations.length > 1) {
          for (int j = 0; j < variations.length; j++) {
            if (variations[j] == term.substring(0, i)) {
              term = term.substring(i);
              break;
            }
          }
        } else {
          term = term.replace(variations[0], "");
          i = 1;
        }
      } else if (i == term.length()) {
        correctAffix = determineCorrectAffix(affixes, term); // FIXME: If this is null then you should return the list of closely related affixes that you found with affixes
        dissectedParts.add(correctAffix);
      }
    }
    return dissectedParts;
  }

  //FIXME: This currently doesn't work for multi-meaning affixes. Such as 'an-' which has two records in database.
  private Affix determineCorrectAffix(List<Affix> affixes, String term) {
    for (int i = 0; i < affixes.size(); i++) {
      String[] variations = findVariations(affixes.get(i));
      for (int j = 0; j < variations.length; j++) {
        if (variations[j].equals(term)) {
          return affixes.get(i);
        }
      }
    }
    return null;
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
