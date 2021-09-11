package com.wild.medicalTermDissector;


import com.wild.medicalTermDissector.affix.Affix;
import com.wild.medicalTermDissector.affix.AffixRepository;
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

  @Autowired
  MedTermDissectorTests(MedTermRepository medTermRepository, AffixRepository affixRepository) {
    this.medTermRepository = medTermRepository;
    this.affixRepository = affixRepository;
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
    final AffixService affixService = new AffixService(affixRepository);
    final Map<String, List<Affix>> dissectedParts = affixService.makeMap(term, possibleAnswers);
    System.out.println("\nResults:");
    System.out.println(dissectedParts.keySet());
    final Object[] arr = dissectedParts.keySet().toArray();
    for (int i = 0; i < arr.length; i++) {
      if (dissectedParts.get(arr[i]) != null) {
        System.out.println(arr[i]);
        System.out.println(dissectedParts.get(arr[i]).get(0).getMeaning());
      } else {
        System.out.println(arr[i]);
        System.out.println("null");
      }
    }
    System.out.println("");
    assertNull(dissectedParts.get("vol"));
  }

  @Test
  public void makeMapFail() {
    final String term = "test";
    final ArrayList<String> possibleAnswers = new ArrayList<>();
    final AffixService affixService = new AffixService(affixRepository);
    assertThrows(IllegalArgumentException.class, () -> {
      affixService.makeMap(term, possibleAnswers);
    });
  }

/*
  @Test
  public void dissectSuccess() {
    String term = "hypoglycemia";
    AffixService affixService = new AffixService(affixRepository);
    Map<String, List<Affix>> dissectedParts = affixService.dissect(term);
    System.out.println("keyset: " + dissectedParts.keySet() + "\n");
    for (Map.Entry<String, List<Affix>> me : dissectedParts.entrySet()) {
      System.out.println(
        "affix: " + dissectedParts.get(me.getKey()).get(0).getAffix() + "\n" +
        "meaning: " + dissectedParts.get(me.getKey()).get(0).getMeaning() + "\n" +
        "examples: " + dissectedParts.get(me.getKey()).get(0).getExamples() + "\n"
      );
    }
    assertEquals("hyp(o)-", dissectedParts.get("hypo").get(0).getAffix());
    assertEquals("below normal", dissectedParts.get("hypo").get(0).getMeaning());
  }
*/

  @Test
  public void testMissingAffix() {
    // Ideally this should return "hypo: lack of, vol: null, emia: blood"
    // I should probably make testMissingPrefix and testMissingSuffix as well. Just so I can make sure
    // algorithm can handle missing parts from any part of the word.
    String term = "hypovolemia";
    AffixService affixService = new AffixService(affixRepository);
    Map<String, List<Affix>> dissectedParts = affixService.dissect(term);
    System.out.println("\nResults:");
    System.out.println(dissectedParts.keySet());
    final Object[] arr = dissectedParts.keySet().toArray();
    for (int i = 0; i < arr.length; i++) {
      if (dissectedParts.get(arr[i]) != null) {
        System.out.println(arr[i]);
        System.out.println(dissectedParts.get(arr[i]).get(0).getMeaning());
      } else {
        System.out.println(arr[i]);
        System.out.println("null");
      }
    }
    System.out.println("");
    assertNull(dissectedParts.get("vol"));

/*
    final Affix[] values = dissectedParts.values().toArray(new Affix[dissectedParts.size()]);
    for (int i = 0; i < values.length; i++) {
      System.out.println(values[i].getAffix());
      System.out.println(values[i].getMeaning());
    }
    assertEquals("hyp(o)-", values[0].getAffix());
*/
/*
    System.out.println(dissectedParts.get(0).getAffix());   // hypo-
    System.out.println("meaning: " + dissectedParts.get(0).getMeaning()); // below normal
    System.out.println(dissectedParts.get(1).getAffix());   // "vol"
    System.out.println("meaning: " + dissectedParts.get(1).getMeaning()); // null
    System.out.println(dissectedParts.get(2).getAffix());
    System.out.println("meaning: " + dissectedParts.get(2).getMeaning());
    assertEquals("hyp(o)-", dissectedParts.get(0).getAffix());
    assertEquals("below normal", dissectedParts.get(0).getMeaning());
    assertEquals(null, dissectedParts.get(1).getMeaning());
    assertEquals("blood condition (Am. Engl.),blood", dissectedParts.get(2).getMeaning());
*/
  }

/*
  @Test
  public void testMultipleWords() {
  // This also tests how the algorithm handles more than one root/affix in a word.
    String term = "Sphenopalatine Ganglioneuralgia";
  }
*/

/*
  @Test
  public void testTwoLetterParentheses() {
    // "-alge(si)" is a affix with double letter parentheses. (COMPLETE)

    // Furthermore a-, an-, and ana- are all real affixes, but only one will work in this instance.
    // How can algorithm choose the correct one every time? Answer: The algorithm needs to choose the smallest one and
    // then check if the next part of the word creates a complete affix on its own, or if it only can do so after
    // reducing one of the letters from itself. If it must remove a letter from beginning then that means the beginning affix needs to add a letter.

    // Also 'an-' actually appears twice in affix list because it has two different meanings based on context.
    // In these instances it should return both affixes and let the user choose which one makes more sense.
    // After the user chooses the one that makes the most sense, it should send me a message and then I will make the
    // algorithm automatically choose that option for that word from then on. (HALF-COMPLETE)

    // Also algesic is not a affix that exists. (c) is missing. How should it handle that?
    // c is only one letter so it shouldn't even be considered a dissected part. (COMPLETE)
    String term = "analgesic";
    AffixService affixService = new AffixService(affixRepository);
    Map<String, List<Affix>> dissectedParts = affixService.dissect(term);
*/
/*
    System.out.println(dissectedParts.get(0).getAffix());
    System.out.println("meaning: " + dissectedParts.get(0).getMeaning());
    System.out.println(dissectedParts.get(1).getAffix());
    System.out.println("meaning: " + dissectedParts.get(1).getMeaning());
    System.out.println(dissectedParts.get(2).getAffix());
    System.out.println("meaning: " + dissectedParts.get(2).getMeaning());
    System.out.println(dissectedParts.get(0).getExamples());
    assertEquals("an-", dissectedParts.get(0).getAffix());
    assertEquals("anus", dissectedParts.get(0).getMeaning()); //"an-"
    assertEquals("pain", dissectedParts.get(1).getMeaning()); //"alge(si)"
    assertEquals(null, dissectedParts.get(2)); //"c"
*//*

  }
*/

/*
  @Test
  public void testRootWord() {
    String term = "antibody";
    AffixService affixService = new AffixService(affixRepository);
    Map<String, List<Affix>> dissectedParts = affixService.dissect(term);
*/
/*
    List<Affix> dissectedParts = affixService.dissect(term);
    System.out.println(dissectedParts.get(0).getAffix());
    System.out.println("meaning: " + dissectedParts.get(0).getMeaning());
    System.out.println(dissectedParts.get(1).getAffix());
    System.out.println("meaning: " + dissectedParts.get(1).getMeaning());
    System.out.println(dissectedParts.get(2).getAffix());
    System.out.println("meaning: " + dissectedParts.get(2).getMeaning());
    System.out.println(dissectedParts.get(0).getExamples());
    assertEquals("an-", dissectedParts.get(0).getAffix());
    assertEquals("anus", dissectedParts.get(0).getMeaning()); //"an-"
    assertEquals("pain", dissectedParts.get(1).getMeaning()); //"alge(si)"
    assertEquals(null, dissectedParts.get(2)); //"c"
*//*

  }
*/

}

/*
  @Test
  public void customMysqlQueryTest() {

  }
*/

/* //FIXME: This is a good test for normal Java, but in spring it won't work due to the fact spring tests require very special handling, especially due to spring dependency injection preventing normal unit and integration tests from working.
  @Test   // Source: https://examples.javacodegeeks.com/core-java/java-11-standardized-http-client-api-example/
  public void httpPostTest() throws IOException, InterruptedException {
    HttpClient client = HttpClient.newHttpClient();
    HttpRequest request = HttpRequest.newBuilder()
      .uri(URI.create("http://localhost:8080/api/student"))
      .timeout(Duration.ofSeconds(15))
      .header("Content-Type", "application/json")
      .POST(HttpRequest.BodyPublishers.ofString("name=betsy&email=betsy@mail.com&dob=1992-08-06"))  // HttpRequest.BodyPublishers.ofFile(Paths.get("file.json")) This is how to get from a file instead of string.
      .build();
    HttpResponse response = client.send(request, HttpResponse.BodyHandlers.discarding());
    assertTrue(response.statusCode() == 201, "Status Code is not Created");
  }


  @Test
    public void httpGetTest() {
    HttpClient client = HttpClient.newBuilder()
      .version(HttpClient.Version.HTTP_2)
      .followRedirects(HttpClient.Redirect.NORMAL)
      .connectTimeout(Duration.ofSeconds(10))
//      .proxy(ProxySelector.of(new InetSocketAddress("www-proxy.com", 8080)))
      .authenticator(Authenticator.getDefault())
      .build();
  }
*/

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

//  @Test
//  public void GetStudentFail() {
//
//  }
