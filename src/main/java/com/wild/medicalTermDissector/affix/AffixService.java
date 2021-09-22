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

  public AffixResult dissect(String term) {
    term = term.trim();
    if (term.contains(" ")) { //FIXME: Never throw exceptions for user input. Change this to return null value. Javascript frontend should do validation checking so this never happens.
      throw new IllegalArgumentException("Multiple words cannot be dissected. Please only type one word at a time.");
    }
    term = term.replaceAll("[^a-zA-Z]", "").toLowerCase(Locale.ROOT);
    final AffixResult affixResult = new AffixResult();
    affixResult.term = term;
    affixResult.definition = findDefinition(term);
    final List<Affix> allPossibleAffixes = affixRepository.findByMedTerm(term);
    if (allPossibleAffixes.size() == 0) {
      LOGGER.debug("This medical term returned no affixes.");
      Map<String, List<Affix>> map = new LinkedHashMap<>();
      map.put(term, null);
      affixResult.affixMap = map;
      return affixResult;
    }
    final ArrayList<String> orderedAffixes = sortAffixesByLength(allPossibleAffixes);
    final ArrayList<String> chosenAffixes = chooseAffixes(orderedAffixes, term);
    final Map<String, List<Affix>> dissectedParts = makeMap(term, chosenAffixes);
    affixResult.affixMap = dissectedParts;
    return affixResult;
  }

  private ArrayList<String> chooseAffixes(ArrayList<String> orderedAffixes, String term) {
    ArrayList<String> chosenAffixes = new ArrayList<>();
    String newTerm = term;
    for (int i = orderedAffixes.size(); i > 0; i--) {
      if (newTerm.replace(orderedAffixes.get(i-1), "").length() != newTerm.length()) {
        chosenAffixes.add(orderedAffixes.get(i-1));
        newTerm = newTerm.replace(orderedAffixes.get(i-1), "");
      } else {
        continue;
      }
    }
    return chosenAffixes;
  }

  private ArrayList<String> sortAffixesByLength(List<Affix> allPossibleAffixes) {
    ArrayList<String> affixes = new ArrayList<>();
    int[] affixLengths = new int[allPossibleAffixes.size()];
    for (int i = 0; i < allPossibleAffixes.size(); i++) {
      allPossibleAffixes.get(i).getReadableAffix().length();
      affixes.add(allPossibleAffixes.get(i).getReadableAffix());
      affixLengths[i] = affixes.get(i).length();
    }
    int minNum = affixLengths[0];
    String minWord = affixes.get(0);
    int minIndex = 0;
    boolean smallerFound = false;
    for (int j = 0; j < affixLengths.length - 1; j++) {
      for (int i = j; i < affixLengths.length; i++) {
        if (affixLengths[i] < minNum) {
          minNum = affixLengths[i];
          minWord = affixes.get(i);
          minIndex = i;
          smallerFound = true;
        }
      }
      if (smallerFound == true) {
        affixLengths[minIndex] = affixLengths[j];
        affixes.set(minIndex, affixes.get(j));
        affixLengths[j] = minNum;
        affixes.set(j, minWord);
        smallerFound = false;
      }
      minNum = affixLengths[j + 1];
      minWord = affixes.get(j + 1);
    }
    return affixes;
  }

  public Map<String, List<Affix>> makeMap(String term, ArrayList<String> possibleAnswers) {
    List<Affix> affixes;
    String newTerm = term;
    if (possibleAnswers.size() == 0) {
      Map<String, List<Affix>> map = new LinkedHashMap<>();
      map.put(term,null);
      return map;
    }
    for (int i = 0; i < possibleAnswers.size(); i++) {
      newTerm = newTerm.replace(possibleAnswers.get(i), " ").trim();
    }
    if (newTerm.length() != 0) {
      final String[] nullAffixes = newTerm.split(" ");
      for (int i = 0; i < nullAffixes.length; i++) {
        possibleAnswers.add(nullAffixes[i]);
      }
    }
    Map<String, List<Affix>> map = new LinkedHashMap<>();
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
    for (int i = 0; i < arr.length; i++) {
      if (i == 0 && arr[i] != null) {
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

  private String findDefinition(String term) {
    // This method must look inside medTerm table and find the definition of the medical term. If it's not located in there,
    // then this method must look online and find a definition and add it to the database.
    return "definition goes here";
  }

  //TODO: Make this print out the original term and it's definition as well as printing all the affixes and their definitions.
  public void printDissectedParts(AffixResult dissectedParts) {
    System.out.println("\nResults:");
    System.out.println(dissectedParts.affixMap.keySet());
    final Object[] arr = dissectedParts.affixMap.keySet().toArray();
    for (int i = 0; i < arr.length; i++) {
      System.out.println("\naffix: " + arr[i]);
      if (dissectedParts.affixMap.get(arr[i]) != null) {
        for (int j = 0; j < dissectedParts.affixMap.get(arr[i]).size(); j++) {
          System.out.println("meaning #" + (j + 1) + ": " + dissectedParts.affixMap.get(arr[i]).get(j).getMeaning());
          System.out.println("examples #" + (j + 1) + ": " + dissectedParts.affixMap.get(arr[i]).get(j).getExamples());
        }
      } else {
        System.out.println("null");
      }
    }
    System.out.println("\nfull definition of " + dissectedParts.term + ": " + dissectedParts.definition + "\n");
  }

}