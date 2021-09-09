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
    String newTerm = term;
    String subTerm = "";
    String exact = "exact";
    String relative = "relative";
    List<Affix> affixes;
    String[] possibleAnswers = new String[10];
    Map<String, Integer> returnedResultSizes = new HashMap<>();
    for (int i = 1; i < term.length(); i++) {
      if (term.equals(newTerm)) {  // Check if we are working on getting the first section (prefix)
        if (!newTerm.substring(0, i).equals('a')) {  // For prefixes only
          i++;
        }
        subTerm = newTerm.substring(0, i);
        affixes = affixRepository.findByExactAffix(subTerm, true);
        returnedResultSizes.put(exact, affixes.size());
        if (returnedResultSizes.get(exact) > 0) {  // If one or more exact matches exist for 'an' for example.
          possibleAnswers[0] = affixes.get(0).getAffix();
        } else {
          affixes = affixRepository.findByAffixStartsWith(subTerm, true);
          returnedResultSizes.put(relative, affixes.size());
          if (returnedResultSizes.get(relative) == 1) {
            possibleAnswers[0] = affixes.get(0).getAffix();
          } else if (returnedResultSizes.get(exact) == 0 && returnedResultSizes.get(relative) == 0) { // should be rare.
            // put some method here to handle cases where a prefix is not found that exists in the database. Hypothetically: Imagine if 'hypo' didn't exist.
          } else {
            continue;
          }
        }
        subTerm = chooseCorrectVariation(findVariations(affixes.get(0)), subTerm);
        newTerm = newTerm.replace(subTerm, "");  // 'hypovolemia' becomes 'volemia'
      } else { // At this point we just past FE(hypo) and chose it for prefix
        if (!newTerm.substring(0, i).equals('y')) { // For suffixes only
          i++;
        }
        affixes = affixRepository.findByExactAffix(subTerm, false);  //FE('vo')
        returnedResultSizes.put(exact, affixes.size());
        if (returnedResultSizes.get(exact) == 1) {
          possibleAnswers[1] = affixes.get(0).getAffix();
        } else {
          affixes = affixRepository.findByAffixStartsWith(subTerm, false);  //FR('vo')
          returnedResultSizes.put(relative, affixes.size());
          if (returnedResultSizes.get(relative) == 1) {
            possibleAnswers[1] = affixes.get(0).getAffix();
          } else if (returnedResultSizes.get(exact) == 0 && returnedResultSizes.get(relative) == 0) { // 'vo' returns 0 for both.
            // begin reading from end instead.
            for (int j = newTerm.length(); j < 0; j--) {
              if (!newTerm.substring(0, i).equals('y') && newTerm.length() > 1) { // For suffixes only
                j--;
              } else {
                newTerm = possibleAnswers[0] + newTerm; // 'n' becomes 'an'
              }
              subTerm = newTerm.substring(newTerm.length() - j);
              affixes = affixRepository.findByExactAffix(subTerm, false); // fe('ia')
              returnedResultSizes.put(exact, affixes.size());
              if (returnedResultSizes.get(relative) > 0) {
                possibleAnswers[9] = affixes.get(0).getAffix();
              } else {
                affixes = affixRepository.findByAffixEndsWith(subTerm, false); // fr('ia')
                returnedResultSizes.put(relative, affixes.size()); // exact: 0
                if (returnedResultSizes.get(relative) > 0) {
                  possibleAnswers[9] = affixes.get(0).getAffix();  // [ hypo, null, 'ia' ]
                } else {
                  // This means searching from the end couldn't find any valid affix or root. For example hypothetically imagine if 'emia' wasn't in the database.
                  // Later I will work on getting this to actually keep going and read more sophisticated from right to left to keep finding terms from the middle if possible
                  // But for now, since this is exceptionally rare, I will just make it give up on the middle and end. so  'hypo' would be found, but 'volemia' would return null.
                  return makeMap(term, possibleAnswers);
                }
              }
              // remove 'emia' from newTerm and keep going backwards. 'volemia' becomes 'vol' 'nalgesic' becomes 'n'
              subTerm = possibleAnswers[9];
              newTerm = newTerm.replace(subTerm, "");
            }
          } else {
            continue;   // Basically if > 1 exact or relative results exist then run through loop for next letter 'vol' in this case. Except vol won't happen like this since it gets caught in if statement above.
          }
        }
      }
    }
    return makeMap(term, possibleAnswers); // end it here
  }

  public Map<String, List<Affix>> makeMap(String term, String[] possibleAnswers) {
    Map<String, List<Affix>> map = new HashMap<>();
    for (int i = 0; i < possibleAnswers.length; i++) {
      if (i == 0 && possibleAnswers[i] != null) { // Adds a list of the exact matches in database. Most cases there will only be one match, but sometimes there are 2.
        map.put(possibleAnswers[i], affixRepository.findByExactAffix(possibleAnswers[i], true));
        term = term.replace(possibleAnswers[i], "");
      } else if (possibleAnswers[i] != null) {
        map.put(possibleAnswers[i], affixRepository.findByExactAffix(possibleAnswers[i],false));
        term = term.replace(possibleAnswers[i], "");
      }
    }
    map.put(term, null);
    return map;
  }

  // Make code that resets to beginning of the word if it fails. Example: i = 1; newTerm = term;
  // Or you can just make it return null for the final affix instead. In fact, perhaps it would be best if a
  // affix doesn't exist in the database, it just returns null, then if the user thinks that the affix is not
  // including the right number of words, they can click try again, and this time a flag is raised which makes the
  // method do things slightly different, instead of choosing the first option of the first affix, it checks the
  // second possible option if that exists. If that doesn't exist, then the second option of the second affix will
  // be used if that exists. Finally, if that doesn't exist, then it will return the exact same result to the user
  // and then they can manually correct it using the form.

  private String chooseCorrectVariation(String[] variations, String subterm) {
    if (variations.length == 1) {  // if subterm = 'hyp' and variation[0] = 'hypo' it returns 'hypo'.
      return variations[0];
    }
    for (int i = 0; i < variations.length; i++) {
      if (variations[i].equals(subterm)) {   // 'an' == 'a' (false)
        return subterm;
      } else if (variations[i].contains(subterm)) {  // 'an' contains 'a' (true)
        return variations[i];
      }

    }
    return null;
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

/*
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
*/
