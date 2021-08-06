package com.wild.springpractice.student;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
  // Because it extends JpaRepository, it already has basic CRUD methods. Alternative is CRUDRepository. But that doesn't use jpa I think.
//  @Query("SELECT s FROM Student s WHERE s.email = ?1")    // This is JBQL not normal SQL. This is what the code below will run.
  Optional<Student> findStudentByEmail(String email);

// This is how you create custom sql queries with jpa. sometimes this part isn't required like above. Also value isn't required either.
// You define the query on top, then you state the return object and the method name and params if any below.
//  @Query(value = "SELECT s FROM Student s")
//  List<Student> getStudent();

// This is how you can use nativeQuery or in other words mysql for mysql, or couchbase for couchbase etc...
//  @Query(value = "SELECT * FROM student", nativeQuery = true)
//  List<Student> getStudent();
}
