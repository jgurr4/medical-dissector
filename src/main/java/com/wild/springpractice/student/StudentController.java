package com.wild.springpractice.student;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RestController;

import java.time.LocalDate;
import java.util.List;

@RestController
@RequestMapping(path = "/api/student")
public class StudentController {
  @GetMapping      //Since we use this here, it is not required to put method = RequestMethod.GET in @RequestMapping.
  public List<Student> getStudents() {
    return List.of(new Student(1L,
      "John",
      43,
      "john@mail.com",
      LocalDate.of(1995, 04, 23)
    ));
  }
}
//  public List<Student> getStudents(@RequestParam(value = "name", defaultValue = "World") String name) {
// @RequestParam is a useful way to set a default value in case a parameter is not given.
