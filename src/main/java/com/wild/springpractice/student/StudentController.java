package com.wild.springpractice.student;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
@RequestMapping(path = "/api/student")
public class StudentController {

  private final StudentService studentService;

  @Autowired
  public StudentController(StudentService studentService) {
    this.studentService = studentService;
  }

  @GetMapping      //Since we use this here, it is not required to put method = RequestMethod.GET in @RequestMapping.
  public List<Student> getStudents() {
    return studentService.getStudents();
  }
}
// @RequestParam is a useful way to set a default value in case a parameter is not given. Example:
//  public List<Student> getStudents(@RequestParam(value = "name", defaultValue = "World") String name) {
