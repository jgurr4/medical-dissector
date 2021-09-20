package com.wild.medicalTermDissector;


import com.wild.medicalTermDissector.affix.Affix;
import com.wild.medicalTermDissector.affix.AffixRepository;
import com.wild.medicalTermDissector.affix.AffixResult;
import com.wild.medicalTermDissector.affix.AffixService;
import com.wild.medicalTermDissector.medicalTerms.MedTerm;
import com.wild.medicalTermDissector.medicalTerms.MedTermRepository;
import com.wild.medicalTermDissector.medicalTerms.MedTermService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.*;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
class MedTermDissectorTests {

  private final MedTermRepository medTermRepository;
  private final AffixRepository affixRepository;
  private final AffixService affixService;

  @Autowired
  MedTermDissectorTests(MedTermRepository medTermRepository, AffixRepository affixRepository, AffixService affixService) {
    this.medTermRepository = medTermRepository;
    this.affixRepository = affixRepository;
    this.affixService = affixService;
  }


  @BeforeAll
  @Test
  public static void checkMariadb() {
    StringBuffer output = new StringBuffer();
    Process p;
    try {
      p = Runtime.getRuntime().exec("docker ps");
      p.waitFor();
      BufferedReader reader = new BufferedReader(new InputStreamReader(p.getInputStream()));
      String line = "";
      while ((line = reader.readLine()) != null) {
        output.append(line + "\n");
      }
    } catch (Exception e) {
      e.printStackTrace();
    }
    System.out.println(output);
    assertTrue(output.toString().contains("mariadb"));
  }

  @Test
  public void getMedTermsTest() {
    Boolean returnedList = true;
    MedTermService medTermService = new MedTermService(medTermRepository);
    final List<MedTerm> medTerms = medTermService.getMedTerm();
//    System.out.println("\n" + medTerms + "\n");
    if (medTerms == null) {
      returnedList = false;
    }
    assertTrue(returnedList);
  }

  @Test
  public void addMedTermsSuccess() {
    MedTermService medTermService = new MedTermService(medTermRepository);
    List<MedTerm> medTerms = List.of(
      new MedTerm("hyperventilation", "abnormally rapid breathing"),
      new MedTerm("hypertrophy", "increase in the size of an organ due to an increase in the size of its cells"));
    for (MedTerm medTerm : medTerms) {
      medTermService.addMedTerm(medTerm);
    }
    assertTrue(medTermService.getMedTerm("hyperventilation").isPresent());
  }

  @Test
  public void addMedTermSuccess() {
    MedTermService medTermService = new MedTermService(medTermRepository);
    final MedTerm medTerm = new MedTerm("hyperplasia", "the enlargement of an organ or tissue ");
    medTermService.addMedTerm(medTerm);
    assertTrue(medTermService.getMedTerm("hyperplasia").isPresent());
  }

  @Test
  public void findByNameStartsWithSuccess() {
    MedTermService medTermService = new MedTermService(medTermRepository);
    final MedTerm medTerm = new MedTerm("hyperstasis", "something here");
    medTermService.addMedTerm(medTerm);
    final List<MedTerm> medTermsList = medTermService.getMedTerms("hy");
//    System.out.println("\n" + medTermsList + "\n");
    assertFalse(medTermsList.isEmpty());
  }

  @Test
  public void updateMedTermSuccess() {
    String name = "hypoglycemia";
    final MedTermService medTermService = new MedTermService(medTermRepository);
    final MedTerm medTerm = new MedTerm(name, "below normal levels of sugar in blood.");
    medTermService.addMedTerm(medTerm);
    final Optional<MedTerm> optionalMedTerm = medTermService.getMedTerm(name);
    try {
      optionalMedTerm.get().getId();
    } catch (Exception err) {
      System.out.println("\nMedical term doesn't exist.\n");
      fail();
    }
    final Long termId = medTermService.getMedTerm(name).get().getId();
    medTermService.updateMedTerm(termId, name, "Lack of sugar in blood");
    assertTrue(medTermService.getMedTerm(name).isPresent());
  }

  @Test
  public void deleteMedTermSuccess() {
    Boolean testFailed = false;
    final MedTermService medTermService = new MedTermService(medTermRepository);
    final MedTerm medTerm = new MedTerm("removeme", "This term must be removed.");
    medTermService.addMedTerm(medTerm);
    try {
      medTermService.deleteMedTerm(medTerm.getId());
    } catch (Exception err) {
    }
    final Optional<MedTerm> optionalMedTerm = medTermRepository.findById(medTerm.getId());
    if (optionalMedTerm.isPresent()) {
      testFailed = true;
    }
    assertFalse(testFailed);
  }

  @Test
  public void makeMapSuccess() {
    final String term = "hypovolemia";
    final ArrayList<String> possibleAnswers = new ArrayList<>();
    possibleAnswers.add("emia");
    possibleAnswers.add("hypo");
    AffixResult affixResult = new AffixResult();
    affixResult.term = term;
    affixResult.affixMap = affixService.makeMap(term, possibleAnswers);
    affixService.printDissectedParts(affixResult);
    assertNull(affixResult.affixMap.get("vol"));
  }

  @Test
  public void makeMapFail() {
    final String term = "test";
    final ArrayList<String> possibleAnswers = new ArrayList<>();
    final Map<String, List<Affix>> dissectedParts = affixService.makeMap(term, possibleAnswers);
    assertNull(dissectedParts.get("test"));
  }

  @Test
  public void dissectSuccess() {
    String term = "hypoglycemia";
    AffixResult dissectedParts = affixService.dissect(term);
    System.out.println("keyset: " + dissectedParts.affixMap.keySet() + "\n");
    for (Map.Entry<String, List<Affix>> me : dissectedParts.affixMap.entrySet()) {
      System.out.println(
        "affix: " + dissectedParts.affixMap.get(me.getKey()).get(0).getAffix() + "\n" +
          "meaning: " + dissectedParts.affixMap.get(me.getKey()).get(0).getMeaning() + "\n" +
          "examples: " + dissectedParts.affixMap.get(me.getKey()).get(0).getExamples() + "\n"
      );
    }
    assertEquals("hyp(o)-", dissectedParts.affixMap.get("hypo").get(0).getAffix());
    assertEquals("below normal", dissectedParts.affixMap.get("hypo").get(0).getMeaning());
  }


  @Test
  public void testMissingAffix() {
    String term = "hypovolemia";
    AffixResult dissectedParts = affixService.dissect(term);
    affixService.printDissectedParts(dissectedParts);
    assertNull(dissectedParts.affixMap.get("vol"));
  }

  @Test
  public void testOneAffix() {
    String term = "opium";
    AffixResult dissectedParts = affixService.dissect(term);
    affixService.printDissectedParts(dissectedParts);
    assertNull(dissectedParts.affixMap.get("op"));
  }

  @Test
  public void testNoMatches() {
    String term = "op";
    AffixResult dissectedParts = affixService.dissect(term);
    affixService.printDissectedParts(dissectedParts);
    assertNull(dissectedParts.affixMap.get("op"));
  }

  @Test
  public void testTwoLetterParentheses() {
    String term = "analgesic";
    AffixResult dissectedParts = affixService.dissect(term);
    affixService.printDissectedParts(dissectedParts);
    assertEquals("not, without (alpha privative)", dissectedParts.affixMap.get("an").get(0).getMeaning());
    assertEquals("analgesic, apathy", dissectedParts.affixMap.get("an").get(0).getExamples());
    assertEquals("anal", dissectedParts.affixMap.get("an").get(1).getExamples());
  }

  @Test
  public void testBetweenMissingAffixes() {  // This also tests Capital letters.
    String term = "Ganglioneuralgia";
    AffixResult dissectedParts = affixService.dissect(term);
    affixService.printDissectedParts(dissectedParts);
    assertNull(dissectedParts.affixMap.get("g"));
    assertNull(dissectedParts.affixMap.get("glio"));
    assertEquals("of or pertaining to nerves and the nervous system", dissectedParts.affixMap.get("neur").get(0).getMeaning());
//    assertEquals("neurofibromatosis", dissectedParts.get("neur").get(0).getExamples());
  }

  @Test
  public void testMultipleWords() {
    //TODO: Eventually you might want to make it possible for dissect to handle multiple words.
    //FIXME: Do not use exceptions for user input.
    String term = "Sphenopalatine Ganglioneuralgia";
    assertThrows(IllegalArgumentException.class, () -> {
      affixService.dissect(term);
    });
  }

  //TODO: Get term definition lookup working for makeMap/dissect method, so you can test that here too.
  @Test
  public void testTermDefinitionAndSpecialChars() {
    String term = "hypogl*y@/c&emia";
    AffixResult dissectedParts = affixService.dissect(term);
    affixService.printDissectedParts(dissectedParts);
//    assertEquals("An abnormally low level of glucose in the blood.", dissectedParts.get(term.trim().replaceAll("[^a-zA-Z]", "").toLowerCase(Locale.ROOT)).get(0).getMeaning());
  }

/*
  //TODO: This test will work better if I decide to add base words with their definitions to the affix_view table.
  @Test
  public void testRootWord() {
    String term = "antibody";
    AffixService affixService = new AffixService(affixRepository);
    AffixResult dissectedParts = affixService.dissect(term);
    affixService.printDissectedParts(dissectedParts);
//    assertEquals("the main, central, or principal part", dissectedParts.get("body").get(0).getMeaning());
  }
*/

/*
  //TODO: Find a word that repeats a affix and place it here. (If one exists. It might not exist)
  @Test
  public void testRepeatedAffixes() {
    String term = "";
  }
*/

}




// This is only if you are using the persistence.xml, which this project doesn't use.
//  @Test
//  public void mysqlTest() {
//    StudentService studentService = new StudentService(studentRepository);
////    test mysql stuff using EntityManager here:
//    String name = "billy";
//    LocalDate dob = LocalDate.of(1835, 04, 23);
//    String email = "billy@mail.com";
//
//    Student student = new Student();
//    EntityManagerFactory emf = Persistence.createEntityManagerFactory("test1");
//    EntityManager em = emf.createEntityManager();
//    em.getTransaction().begin();
////    em.createNativeQuery("SELECT * FROM student");
//    student.setName(name);
//    student.setDob(dob);
//    student.setEmail(email);
//    em.persist(student);
//    em.getTransaction().commit();
//    em.close();
//    emf.close();
//
//    assertTrue(studentService.getStudent(email).isPresent());
//  }

