package com.wild.medicalTermDissector;


import com.wild.medicalTermDissector.affix.Affix;
import com.wild.medicalTermDissector.affix.AffixRepository;
import com.wild.medicalTermDissector.medicalTerms.MedTerm;
import com.wild.medicalTermDissector.medicalTerms.MedTermRepository;
import com.wild.medicalTermDissector.medicalTerms.MedTermService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Optional;

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

  //FIXME: right now emia returns -aemia and -emia which are completely different affixes. There is no backup plan in case all the letters of term are used, and it still retrieves multiple results. What should it do in that case?
  // Test this with 5 other words, and then also test with a word that I don't have complete or exact affix for.
  // For example: hypovolemia. I don't have affix for 'vol'. what should your function do, if it cannot find a affix which matches or even closely matches?
  // It's possible vol really is a variation of ole, but that would mean emia is mixed with ole. Find out if that is a common thing with medical terms, or if that rarely or never happens.
  // According to the internet hypovolemia is a decrease of blood volume. So vol = volume. Which is where because I cannot find any affix for vol. So maybe I'll make one. It's probably not a medical affix, it could actually be a normal english affix.
  // That might be more common of a problem. Best solution if my function cannot find a matching affix for vol, is to return a exception which states that word has affixes not visible in the database. If that happens, whatever function that called this
  // function should offer to guide user to adding the medical term and it's appropriate affixes to the database.
  @Test
  public void dissectSuccess() {
    String term = "hypoglycemia";
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
    System.out.println(dissectedParts.get(0).getAffix());
    System.out.println(dissectedParts.get(0).getMeaning());
    System.out.println(dissectedParts.get(1).getMeaning());
    System.out.println(dissectedParts.get(2).getMeaning());
    System.out.println(dissectedParts.get(0).getExamples());
    assertEquals("hyp(o)-", dissectedParts.get(0).getAffix());
    assertEquals("below normal", dissectedParts.get(0).getMeaning()); //"hypo-"
    assertEquals("sugar", dissectedParts.get(1).getMeaning()); //"glyc-"
    assertEquals("blood condition (Am. Engl.),blood", dissectedParts.get(2).getMeaning()); //"-emia"
//    assertEquals("hypovolemia, hypoxia", dissectedParts.get(0).getExamples());    // For some reason, all my examples columns have weird newlines and I can't get rid of them in database. Figure that out then this test will work.
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

  @Test
  public void notExactAffixTest() {
    // Test any word here that contains a variation of an affix that isn't in my database yet.
    // Any medical terms in the database which the user searches for and chooses will have affixes.
    // If affix variations are missing. Then a user must add them manually after they add the med term.
    // If a medical term typed by a user doesn't exist, when they dissect it, it automatically adds that med_term
    // to the database, and will also detect closely related affixes for dissection display to user. If the user
    // disagrees with a affix being used, they will be given an option to manually choose the correct affixes for the
    // med_term. If the relevant affixes do not exist, or the meaning exists, but the correct variation doesn't exist, then
    // a user has the ability to add their own affixes as well.
  }

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
