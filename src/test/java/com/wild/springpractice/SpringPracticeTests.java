package com.wild.springpractice;


import com.wild.springpractice.student.Student;
import com.wild.springpractice.student.StudentRepository;
import com.wild.springpractice.student.StudentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
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
  public void saveStudentsSuccess() {
    StudentService studentService = new StudentService(studentRepository);
    List<Student> students = List.of(
      new Student("Dan", "dan@mail.com", LocalDate.of(1992, Month.JANUARY, 24)),
      new Student("mary", "mary@mail.com", LocalDate.of(1994, Month.APRIL, 5)));
    for (Student student : students) {
      studentService.saveStudent(student);
    }
    assertTrue(studentService.getStudent("john@mail.com").isPresent());
  }

  @Test
  public void saveStudentSuccess() {
    StudentService studentService = new StudentService(studentRepository);
    final Student student = new Student("john", "john@mail.com", LocalDate.of(2002, Month.JANUARY, 2));
    studentService.saveStudent(student);
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
    final String email = "john@mail.com";
    final StudentService studentService = new StudentService(studentRepository);
    final Optional<Student> student = studentService.getStudent(email);
    try {
      student.get().getId();
    } catch (Exception err) {
      System.out.println("\nStudent doesn't exist.\n");
      fail();
    }
    final Long id = studentService.getStudent(email).get().getId();
    studentService.updateStudent(new Student(id, "joseph", email, LocalDate.of(1943, 10, 05)));
    assertTrue(student.get().getName().equals("joseph"));
  }

  @Test
  public void removeStudentSuccess() {
    Boolean testFailed = false;
    final String email = "john@mail.com";
    StudentService studentService = new StudentService(studentRepository);
    try {
      studentService.removeStudent(email);
    } catch (Exception err){ }
    final Optional<Student> student = studentService.getStudent(email);
    if (student.isPresent()) {
      testFailed = true;
    }
    assertFalse(testFailed);
  }

  @Test
  public void mysqlTest() { //FIXME: The Persistence.createEntityManagerFactory line is failing. Probably due to not having a persistence.xml file.
    StudentService studentService = new StudentService(studentRepository);
//    test mysql stuff using EntityManager here:
    String name = "billy";
    LocalDate dob = LocalDate.of(1835, 04, 23);
    String email = "billy@mail.com";

    Student student = new Student();
    EntityManagerFactory emf = Persistence.createEntityManagerFactory("test1");
    EntityManager em = emf.createEntityManager();
    em.getTransaction().begin();
//    em.createNativeQuery("SELECT * FROM student");
    student.setName(name);
    student.setDob(dob);
    student.setEmail(email);
    em.persist(student);
    em.getTransaction().commit();
    em.close();
    emf.close();

    assertTrue(studentService.getStudent(email).isPresent());

  }

//  @Test
//  public void GetStudentFail() {
//
//  }

}