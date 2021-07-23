package com.wild.springpractice;


import com.wild.springpractice.student.Student;
import com.wild.springpractice.student.StudentRepository;
import com.wild.springpractice.student.StudentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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
    StudentService studentService = new StudentService(studentRepository);
    final List<Student> students = studentService.getStudents();
    System.out.println(students);
    assertEquals(true, students.isEmpty());
  }

  @Test
  public void getOneStudent() {
  }

  @Test
  public void saveStudentsTest() {
    StudentService studentService = new StudentService(studentRepository);
    List<Student> students = List.of(
      new Student(1, "john", 24, "john@mail.com", LocalDate.of(2002, Month.JANUARY, 2)),
      new Student(2, "mary", 27, "mary@mail.com", LocalDate.of(1994, Month.APRIL, 5)));
    assertEquals("success", studentService.saveStudents(students));
  }

//  @Test
//  public void GetStudentFail() {
//
//  }

}