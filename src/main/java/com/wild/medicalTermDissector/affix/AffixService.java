package com.wild.medicalTermDissector.affix;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.lang.reflect.Array;
import java.util.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Service
public class AffixService {

  private final AffixRepository affixRepository;
  private static final Logger LOGGER = LoggerFactory.getLogger(AffixService.class);

  @Autowired
  public AffixService(AffixRepository affixRepository) {
    this.affixRepository = affixRepository;
  }

  public Map<String, List<Affix>> dissect(String term) {
    final List<Affix> allPossibleAffixes = affixRepository.findByMedTerm(term);
    ArrayList<String> orderedAffixes = sortAffixesByLength(allPossibleAffixes);
    if (orderedAffixes == null) {
      LOGGER.debug("This medical term returned no affixes.");
      return null;
    }
    ArrayList<String> chosenAffixes = chooseAffixes(orderedAffixes, term);
    final Map<String, List<Affix>> dissectedParts = makeMap(term, chosenAffixes);
    LOGGER.info("\nResults:");
    LOGGER.info(String.valueOf(dissectedParts.keySet()));
    final Object[] arr = dissectedParts.keySet().toArray();
    for (int i = 0; i < arr.length; i++) {
      if (dissectedParts.get(arr[i]) != null) {
        LOGGER.info(arr[i].toString());
        LOGGER.info(dissectedParts.get(arr[i]).get(0).getMeaning());
      } else {
        LOGGER.info(arr[i].toString());
        LOGGER.info("null");
      }
    }
    LOGGER.info("");
    return dissectedParts;
  }

  private ArrayList<String> chooseAffixes(ArrayList<String> orderedAffixes, String term) {
    ArrayList<String> chosenAffixes = new ArrayList<>();
    String newTerm = term;
    // Start removing affixes from larges to smallest. That way, if a smaller affix removal doesn't make any change that
    // means a larger affix already took care of it. So any affix that is successfully replaced is added to the chosen affixes list.
    for (int i = orderedAffixes.size() - 1; i > 0; i--) {
      if (newTerm.replace(orderedAffixes.get(i), "").length() != newTerm.length()) {
        chosenAffixes.add(orderedAffixes.get(i));
        newTerm = newTerm.replace(orderedAffixes.get(i), "");
      } else {
        continue;
      }
    }
    return chosenAffixes;
  }

  private ArrayList<String> sortAffixesByLength(List<Affix> allPossibleAffixes) {
    if (allPossibleAffixes.size() == 0) {
      return null;
    }
    ArrayList<String> affixes = new ArrayList<>();
    int[] affixesLength = new int[allPossibleAffixes.size()];
    for (int i = 0; i < allPossibleAffixes.size(); i++) {
      allPossibleAffixes.get(i).getReadableAffix().length();
      affixes.add(allPossibleAffixes.get(i).getReadableAffix()); // Makes a list of readable affixes
      affixesLength[i] = affixes.get(i).length();                // Makes a array of lengths for each affix.
    }
    // Using the list of affixes and the list of lengths sort the lists to be largest to smallest.
    int minNum = affixesLength[0];
    String minWord = affixes.get(0);
    int minIndex = 0;
    boolean smallerFound = false;
    for (int j = 0; j < affixesLength.length - 1; j++) {
      for (int i = j; i < affixesLength.length; i++) {
        if (affixesLength[i] < minNum) {
          minNum = affixesLength[i];
          minWord = affixes.get(i);
          minIndex = i;
          smallerFound = true;
        }
      }
      if (smallerFound == true) {
        affixesLength[minIndex] = affixesLength[j];
        affixes.set(minIndex, affixes.get(j));
        affixesLength[j] = minNum;
        affixes.set(j, minWord);
        smallerFound = false;
      }
      minNum = affixesLength[j + 1];
      minWord = affixes.get(j + 1);
    }
    LOGGER.info(Arrays.toString(affixesLength));
    LOGGER.info(affixes.toString());
    return affixes;
  }

/*
    // This method finds all the ranges of each affix and tries to check which ones overlap each other.
    int start = 0;
    int end = 0;
    int start2 = 0;
    int end2 = 0;
    Map<String, List<Integer>> ranges = new HashMap<>();
    for (int i = 0; i < allPossibleAffixes.size(); i++) {
      affixName = allPossibleAffixes.get(i).getReadableAffix();
      start = term.indexOf(affixName);     // 'hypo' would be 0
      end = term.lastIndexOf(affixName);   // 'hypo' would be 3
      ranges.put(affixName, List.of(start, end));
    }
    //find all the affixes which overlap each other:
    for (int i = 0; i < ranges.size() - 1; i++) {
      start = ranges.get(i).get(0);
      end = ranges.get(i).get(1);
      start2 = ranges.get(i + 1).get(0);
      end2 = ranges.get(i + 1).get(1);
      if (start >= start2 && start <= end2 || end <= end2 && end >= start2) { // this checks if any affix overlaps.
      } else {
      }
//      chosenAffixes.add(allPossibleAffixes.get(i).getReadableAffix());
    }
*/

  private Boolean determineIfValid(String subTerm, String newTerm) {
    if (newTerm.contains(subTerm)) {
      return true;
    }
    return false;
  }

  public Map<String, List<Affix>> makeMap(String term, ArrayList<String> possibleAnswers) {
    if (possibleAnswers.size() == 0) {
      throw new IllegalArgumentException("no value present in ArrayList");
    }
    List<Affix> affixes;
    String newTerm = term;
    for (int i = 0; i < possibleAnswers.size(); i++) {
      newTerm = newTerm.replace(possibleAnswers.get(i), "");
    }
    if (newTerm.length() != 0) {
      possibleAnswers.add(newTerm);
    }
    Map<String, List<Affix>> map = new LinkedHashMap<>();
    // create string array and sort the array based on where each part appears in the term provided.
    String[] arr = possibleAnswers.toArray(new String[possibleAnswers.size()]);
    String minValue = arr[0];
    int minIndex = 0;
    for (int j = 0; j < arr.length - 1; j++) {
      for (int i = j; i < arr.length; i++) {
        if (term.indexOf(arr[i]) < term.indexOf(minValue)) {
          minValue = arr[i];
          minIndex = i;
        }
      }
      arr[minIndex] = arr[j];
      arr[j] = minValue;
      minValue = arr[j + 1];
    }
    System.out.println(Arrays.toString(arr));
    for (int i = 0; i < arr.length; i++) {
      if (i == 0 && arr[i] != null) { // check if it 100% is a prefix.
        // Adds a list of the exact matches in database. Most cases there will only be one match, but sometimes there are 2.
        affixes = affixRepository.findByExactAffix(arr[i]);
        if (affixes.size() == 0) {
          map.put(arr[i], null);
        } else {
          map.put(arr[i], affixes);
        }
      } else if (arr[i] != null) {
        affixes = affixRepository.findByExactAffix(arr[i]);
        if (affixes.size() == 0) {
          map.put(arr[i], null);
        } else {
          map.put(arr[i], affixes);
        }
      }
    }
    return map;
  }

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
