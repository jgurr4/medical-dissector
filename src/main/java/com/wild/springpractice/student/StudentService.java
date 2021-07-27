package com.wild.springpractice.student;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
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

  public void removeStudent(String email) {
    final Optional<Student> student = studentRepository.findStudentByEmail(email);
    studentRepository.delete(student.get());
  }

  public void updateStudent(Student newStudent) {
    studentRepository.save(newStudent);
  }

}
