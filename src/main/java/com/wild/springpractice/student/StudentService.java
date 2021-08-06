package com.wild.springpractice.student;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.swing.text.html.Option;
import java.time.LocalDate;
import java.util.List;
import java.util.Objects;
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

  public void registerStudent(Student student) {
    Optional<Student> studentOptional = studentRepository.findStudentByEmail(student.getEmail());
    if (studentOptional.isPresent()) {
      throw new IllegalStateException("email taken");
    }
    studentRepository.save(student);
  }

  public void updateStudent(Long studentId, String name, String email, String dob) {
    Student student = studentRepository.findById(studentId).orElseThrow(() -> new IllegalStateException("Student with id " + studentId + " does not exist."));
    Student newStudent = new Student(studentId, student.getName(), student.getEmail(), student.getDob());
    if (name != null && name.length() > 0 && !Objects.equals(student.getName(), name)) {
      newStudent.setName(name);
    }
    if (email != null && email.length() > 0 && !Objects.equals(student.getEmail(), email)) {
      Optional<Student> studentOptional = studentRepository.findStudentByEmail(email);
      if (studentOptional.isPresent()) {
        throw new IllegalStateException("Email taken");
      }
      newStudent.setEmail(email);
    }
    if (dob != null && dob.length() > 0 && !Objects.equals(student.getDob(), dob)) {
      newStudent.setDob(LocalDate.parse(dob));
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
