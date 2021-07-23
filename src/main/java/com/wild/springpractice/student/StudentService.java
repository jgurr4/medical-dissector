package com.wild.springpractice.student;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.time.LocalDate;
import java.util.List;

@Service
public class StudentService {

  private final StudentRepository studentRepository;

  @Autowired
  public StudentService(StudentRepository studentRepository) {
    this.studentRepository = studentRepository;
  }

  public List<Student> getStudents() {
    return studentRepository.findAll();
  }

  public String saveStudent(Student student) {
    return studentRepository.save(student).getName() == student.getName() ? "success" : "failure";
  }

  public String saveStudents(List<Student> students) {
    final List<Student> savedList = studentRepository.saveAll(students);
    System.out.println(savedList);
    return savedList.size() == students.size() ? "success" : "failure";
  }

}
