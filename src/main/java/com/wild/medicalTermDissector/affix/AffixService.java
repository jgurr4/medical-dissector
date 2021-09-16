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
    term = term.toLowerCase(Locale.ROOT);
    final List<Affix> allPossibleAffixes = affixRepository.findByMedTerm(term);
    if (allPossibleAffixes.size() == 0) {
      LOGGER.debug("This medical term returned no affixes.");
      return null;
    }
    final ArrayList<String> orderedAffixes = sortAffixesByLength(allPossibleAffixes);
    final ArrayList<String> chosenAffixes = chooseAffixes(orderedAffixes, term);
    final Map<String, List<Affix>> dissectedParts = makeMap(term, chosenAffixes);
    return dissectedParts;
  }

  private ArrayList<String> chooseAffixes(ArrayList<String> orderedAffixes, String term) {
    ArrayList<String> chosenAffixes = new ArrayList<>();
    String newTerm = term;
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
    ArrayList<String> affixes = new ArrayList<>();
    int[] affixesLength = new int[allPossibleAffixes.size()];
    for (int i = 0; i < allPossibleAffixes.size(); i++) {
      allPossibleAffixes.get(i).getReadableAffix().length();
      affixes.add(allPossibleAffixes.get(i).getReadableAffix());
      affixesLength[i] = affixes.get(i).length();
    }
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
    return affixes;
  }

  public Map<String, List<Affix>> makeMap(String term, ArrayList<String> possibleAnswers) {
    if (possibleAnswers.size() == 0) {
      throw new IllegalArgumentException("no value present in ArrayList");
    }
    List<Affix> affixes;
    String newTerm = term;
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

  public void printDissectedParts(Map<String, List<Affix>> dissectedParts) {
    System.out.println("\nResults:");
    System.out.println(dissectedParts.keySet());
    final Object[] arr = dissectedParts.keySet().toArray();
    for (int i = 0; i < arr.length; i++) {
      System.out.println("\naffix: " + arr[i]);
      if (dissectedParts.get(arr[i]) != null) {
        for (int j = 0; j < dissectedParts.get(arr[i]).size(); j++) {
          System.out.println("meaning #" + (j+1) + ": " + dissectedParts.get(arr[i]).get(j).getMeaning());
          System.out.println("examples #" + (j+1) + ": " + dissectedParts.get(arr[i]).get(j).getExamples());
        }
      } else {
        System.out.println("null");
      }
    }
    System.out.println("");
  }
}