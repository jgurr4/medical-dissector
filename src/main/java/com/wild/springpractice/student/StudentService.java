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
  //FIXME: Currently, this will not allow you to update email at same time. probably best solution would be to change http request to different path, and make the content-type something else maybe.
  public void updateStudent(Student newStudent) {
    final boolean exists = studentRepository.existsById(newStudent.getId());
    if (!exists) {
      throw new IllegalStateException("Student with id " + newStudent.getId() + " does not exist.");
    }
    studentRepository.save(newStudent);
  }

  public void removeStudent(Long studentId) {
    final boolean exists = studentRepository.existsById(studentId);
    if (!exists) {
      throw new IllegalStateException("Student with id " + studentId + " does not exist.");
    }
    studentRepository.deleteById(studentId);
  }

}
