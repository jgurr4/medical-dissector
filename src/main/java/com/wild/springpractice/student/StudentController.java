package com.wild.springpractice.student;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping(path = "/api/student")
public class StudentController {
  private final StudentService studentService;

  @Autowired
  public StudentController(StudentService studentService) {
    this.studentService = studentService;
  }

  @GetMapping
  public List<Student> getStudents() {
    return studentService.getStudent();
  }

  @PostMapping
  public void registerNewStudent(@RequestBody Student student) {
    studentService.saveStudent(student);
  }

}
// @RequestParam is a useful way to set a default value in case a parameter is not given. Example:
//  public List<Student> getStudents(@RequestParam(value = "name", defaultValue = "World") String name) {
