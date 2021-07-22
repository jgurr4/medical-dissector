package com.wild.springpractice.student;

import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
public class StudentService {
  public List<Student> getStudents() {
    return List.of(new Student(1L,
      "John",
      43,
      "john@mail.com",
      LocalDate.of(1995, 04, 23)
    ));
  }
}
