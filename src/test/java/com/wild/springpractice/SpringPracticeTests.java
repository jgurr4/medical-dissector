package com.wild.springpractice;


import com.wild.springpractice.student.Student;
import com.wild.springpractice.student.StudentRepository;
import com.wild.springpractice.student.StudentService;
import org.junit.jupiter.api.Test;
import org.junit.runner.manipulation.Ordering;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.persistence.EntityManager;
import javax.persistence.EntityManagerFactory;
import javax.persistence.Persistence;
import java.time.LocalDate;
import java.time.Month;
import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;


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
//    test mysql stuff here:
    Student student = new Student();
    EntityManagerFactory emf = Persistence.createEntityManagerFactory("test1");
    EntityManager em = emf.createEntityManager();
    em.createNativeQuery("SELECT * FROM student");
    em.persist(student);

    StudentService studentService = new StudentService(studentRepository);
    final List<Student> students = studentService.getStudents();
    System.out.println(students);
    if (students == null) {
      returnedList = false;
    }
    assertEquals(true, returnedList);
  }

  @Test
  public void getOneStudent() {
  }

  @Test
  public void saveStudentsTest() {
    StudentService studentService = new StudentService(studentRepository);
    List<Student> students = List.of(
      new Student("john", "john@mail.com", LocalDate.of(2002, Month.JANUARY, 2)),
      new Student("mary", "mary@mail.com", LocalDate.of(1994, Month.APRIL, 5)));
    assertEquals("success", studentService.saveStudents(students));
  }

//  @Test
//  public void GetStudentFail() {
//
//  }

}