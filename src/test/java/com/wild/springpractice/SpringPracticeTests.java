package com.wild.springpractice;


import com.wild.springpractice.student.Student;
import com.wild.springpractice.student.StudentRepository;
import com.wild.springpractice.student.StudentService;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

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
  public void GetStudentsTest() {
    StudentService studentService = new StudentService(studentRepository);
    final List<Student> students = studentService.getStudents();
    System.out.println(students);
    assertEquals(true, students.isEmpty());
  }

  @Test
  public void GetOneStudent() {
  }

  @Test
  public void SaveStudent() {
  }

//  @Test
//  public void GetStudentFail() {
//
//  }

}