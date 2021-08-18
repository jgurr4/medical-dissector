package com.wild.medicalTermDissector.medicalTerms;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface MedTermRepository extends JpaRepository<MedTerm, Long> {
//  @Query("SELECT m FROM medterm m WHERE m.name = ?1")    // This is JBQL not normal SQL.
  Optional<MedTerm> findByName(String name);

  @Query(value = "select * from med_term where name like ?1", nativeQuery = true)   
  List<MedTerm> findByNameStartsWith(String letters);
// Alternatively use Named parameters like this   where name like :title%
// Notice how you don't need quotes to add a % symbol here. You can do same thing with numbered parameters, Test this to see if it works by removing the addition of % in medTermService method.

//  @Query(value = "", nativeQuery = true)
//  Optional<MedTerm> searchByName(String name);

//  List<MedTerm> search(final String words); I would have to create a method for this using a class which implements this interface and defines this method.

// This is how you can use nativeQuery or in other words mysql for mysql, or couchbase for couchbase etc...
//  @Query(value = "SELECT * FROM student", nativeQuery = true)
//  List<Student> getStudent();
}
