package com.wild.springpractice;


import com.wild.springpractice.student.Student;
import com.wild.springpractice.student.StudentRepository;
import com.wild.springpractice.student.StudentService;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.Authenticator;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.Duration;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;


@SpringBootTest
class SpringPracticeTests {

  private final StudentRepository studentRepository;

  @Autowired
  SpringPracticeTests(StudentRepository studentRepository) {
    this.studentRepository = studentRepository;
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
  public void getStudentsTest() {
    Boolean returnedList = true;
    StudentService studentService = new StudentService(studentRepository);
    final List<Student> students = studentService.getStudent();
    System.out.println("\n" + students + "\n");
    if (students == null) {
      returnedList = false;
    }
    assertTrue(returnedList);
  }

  @Test
  public void registerStudentsSuccess() {
    StudentService studentService = new StudentService(studentRepository);
    List<Student> students = List.of(
      new Student("Dan", "dan@mail.com", LocalDate.of(1992, Month.JANUARY, 24)),
      new Student("mary", "mary@mail.com", LocalDate.of(1994, Month.APRIL, 5)));
    for (Student student : students) {
      studentService.registerStudent(student);
    }
    assertTrue(studentService.getStudent("dan@mail.com").isPresent());
  }

  @Test
  public void registerStudentSuccess() {
    StudentService studentService = new StudentService(studentRepository);
    final Student student = new Student("john", "john@mail.com", LocalDate.of(2002, Month.JANUARY, 2));
    studentService.registerStudent(student);
    assertTrue(studentService.getStudent("john@mail.com").isPresent());
  }

  @Test
  public void getStudentByEmailSuccess() {
    Boolean returnedList = true;
    StudentService studentService = new StudentService(studentRepository);
    final Optional<Student> student = studentService.getStudent("john@mail.com");
    System.out.println("\n" + student + "\n");
    if (student == null) {
      returnedList = false;
    }
    assertTrue(returnedList);
  }

  @Test
  public void updateStudentSuccess() {
    String email = "john@mail.com";
    final StudentService studentService = new StudentService(studentRepository);
    final Student student = new Student("john", email, LocalDate.of(2002, Month.JANUARY, 2));
    studentService.registerStudent(student);
    final Optional<Student> optionalStudent = studentService.getStudent(email);
    try {
      optionalStudent.get().getId();
    } catch (Exception err) {
      System.out.println("\nStudent doesn't exist.\n");
      fail();
    }
    final Long id = studentService.getStudent(email).get().getId();
    studentService.updateStudent(id, "joseph", "joseph@mail.com", "1943-10-05");
    assertTrue(studentService.getStudent("joseph@mail.com").isPresent());
  }

  @Test
  public void removeStudentSuccess() {
    Boolean testFailed = false;
    StudentService studentService = new StudentService(studentRepository);
    final Student student = new Student("remove", "removeme@mail.com", LocalDate.of(2002, Month.JANUARY, 2));
    studentService.registerStudent(student);
    try {
      studentService.removeStudent(student.getId());
    } catch (Exception err) {
    }
    final Optional<Student> optionalStudent = studentRepository.findById(student.getId());
    if (optionalStudent.isPresent()) {
      testFailed = true;
    }
    assertFalse(testFailed);
  }

  @Test
  public void customMysqlQueryTest() {

  }

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

}