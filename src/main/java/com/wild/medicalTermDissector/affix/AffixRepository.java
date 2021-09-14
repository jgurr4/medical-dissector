package com.wild.medicalTermDissector.affix;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AffixRepository extends JpaRepository<Affix, Integer> {

/*
  @Query(value = "select * from affix where prefix = ?2 and readAffix regexp concat('^', ?1) or readAffix regexp concat(' ', ?1)", nativeQuery = true)
  List<Affix> findByAffixStartsWith(String letters, Boolean isPrefix);
*/
  @Query(value = "select * from affix where readAffix regexp concat('^', ?1) or readAffix regexp concat(' ', ?1)", nativeQuery = true)
  List<Affix> findByAffixStartsWith(String letters);

/*
  @Query(value = "select * from affix where prefix = ?2 and readAffix regexp concat(?1, '$') or readAffix regexp concat(?1, ',')", nativeQuery = true)
  List<Affix> findByAffixEndsWith(String letters, Boolean isPrefix);
*/
  @Query(value = "select * from affix where readAffix regexp concat(?1, '$') or readAffix regexp concat(?1, ',')", nativeQuery = true)
  List<Affix> findByAffixEndsWith(String letters);

/*
  @Query(value = "select * from affix where prefix = ?2 and readAffix regexp concat('^', ?1, '$') or readAffix regexp concat('^', ?1, ',') or readAffix regexp concat(' ', ?1, ',') or readAffix regexp concat(' ', ?1, '$')", nativeQuery = true)
  List<Affix> findByExactAffix(String letters, Boolean isPrefix);
*/

  @Query(value = "select * from affix where readAffix regexp concat('^', ?1, '$') or readAffix regexp concat('^', ?1, ',') or readAffix regexp concat(' ', ?1, ',') or readAffix regexp concat(' ', ?1, '$')", nativeQuery = true)
  List<Affix> findByExactAffix(String letters);
}
