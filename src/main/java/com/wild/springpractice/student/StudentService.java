package com.wild.springpractice.student;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;

@Service
public class StudentService {

  private final StudentRepository studentRepository;

  @Autowired
  public StudentService(StudentRepository studentRepository) {
    this.studentRepository = studentRepository;
  }

  public List<Student> getStudent() {
    return studentRepository.findAll();
  }

  public Optional<Student> getStudent(String email) {
    return studentRepository.findStudentByEmail(email);
  }

  public void saveStudent(Student student) {
    Optional<Student> studentOptional = studentRepository.findStudentByEmail(student.getEmail());
    if (studentOptional.isPresent()) {
      throw new IllegalStateException("email taken");
    }
    studentRepository.save(student);
  }

  public String saveStudents(List<Student> students) {
    final List<Student> savedList = studentRepository.saveAll(students);
    System.out.println(savedList);
    return savedList.size() == students.size() ? "success" : "failure";
  }

}
