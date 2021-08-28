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
//    final HashMap<Integer, Affix> dissectedParts = new HashMap<>();
    List<Affix> dissectedParts = new ArrayList<>();
    for (int i = 1; i <= term.length(); i++) {    // hyp\\(?o\\)?  should return hypo- or hyp(o)-
      affixes = affixRepository.findByAffixStartsWith(term.substring(0, i).replaceAll("[a-z]-?$", "\\\\(?" + term.substring(i - 1, i) + "\\\\)?")); //FIXME: This only works on one letter at a time. But some affixes have two letters inside parentheses like this: // hem(at)-, haem(ato)-
      if (affixes.size() == 1) {
        // Add affixes object to map using id as key.
        dissectedParts.add(affixes.get(0));
        String[] variations = findVariations(affixes.get(0));
        // check if more than one variation exists, in which case existing term is compared. If only one exists, then it removes that affix from term and returns term.
        if (variations.length > 1) {
          for (int j = 0; j < variations.length; j++) {  // Basically this checks if the term matches any affixes in the list of variations exactly. If it doesn't match exactly it runs the above code again with another letter added on until it does match exactly. If it gets through entire word without matching exactly, then that means no affix exists in database for that word.
            if (variations[j] == term.substring(0, i)) {
              term = term.substring(i);
              break;
            }
          }
        } else {
          term = term.replace(variations[0], ""); // remove affix from term.
          i = 1;                                             // reset i to start loop with remaining chars.
        }
      } else if (i == term.length()) { // If we reach end of the loop and still not narrowed down to one affixes object.  This code will run which determines which affixes most closely relates. First, it
        correctAffix = determineCorrectAffix(affixes, term);
        dissectedParts.add(correctAffix);
      }
    }
    return dissectedParts;
  }

  // This currently only works for end of words. There is no easy way to make it work for words in middle or beginning.
  // To prevent that, just make sure you update affixes table with the most affixes possible, and your med-Terms table should have as many med-terms as possible.

  private Affix determineCorrectAffix(List<Affix> affixes, String term) {
    // using variations of each affix in the list, compares each one to the term. The one which matches the closest is
    // the one chosen. for example the last four letters "emia" may pull up both aemia and emia. The one that is a closer
    // match is emia because that has 100% of letters, and nothing extra.
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
    // This removes special chars like [n] () and - from affixes so that it will be easier to compare
    // to med term substrings.
    return affix.getAffix()
      .replaceAll("\\[[0-9]\\]", "")
      .replace("(", "")
      .replace(")", "")
      .replace("-", "")  // todo: test if .split works when there are no commas to make a single-element array.
      .split(",");  // At this point -algia, alg(i)o- would become { algia, algio } so now it's easier to compare to term.substring(0, i)
  }

}
