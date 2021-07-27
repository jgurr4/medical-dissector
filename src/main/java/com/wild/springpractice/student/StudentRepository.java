package com.wild.springpractice.student;

import com.wild.springpractice.student.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
  // Because it extends JpaRepository, it already has basic CRUD methods. Alternative is CRUDRepository. But that doesn't use jpa I think.
//  @Query("SELECT s FROM Student s WHERE s.email = ?1")    // This is JBQL not normal SQL. This is what the code below will run.
  Optional<Student> findStudentByEmail(String email);
}
