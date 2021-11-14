package com.wild.medicalTermDissector.affix;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface AffixRepository extends JpaRepository<Affix, Integer>, CustomAffixRepository {

  @Query()
  List<Affix> findByAffixFullText(String letters);

  @Query()
  List<Affix> findByMedTerm(String medTerm);

  @Query()
  List<Affix> findByExactAffix(String letters);

/*
  @Query(value = "select * from affix_view where readable_affix regexp concat('^', ?1) or readable_affix regexp concat(' ', ?1)", nativeQuery = true)
  List<Affix> findByAffixStartsWith(String letters);

  @Query(value = "select * from affix_view where readable_affix regexp concat(?1, '$') or readable_affix regexp concat(?1, ',')", nativeQuery = true)
  List<Affix> findByAffixEndsWith(String letters);

  @Query(value = "select * from affix_view where readable_affix regexp concat('^', ?1, '$') or readable_affix regexp concat('^', ?1, ',') or readable_affix regexp concat(' ', ?1, ',') or readable_affix regexp concat(' ', ?1, '$')", nativeQuery = true)
  List<Affix> findByExactAffix(String letters);

  @Query(value = "select id, affix, meaning, examples, readable_affix, locate(readable_affix, ?1) as is_match from affix_view having is_match > 0", nativeQuery = true)
  List<Affix> findByMedTerm(String medTerm);
*/
}
