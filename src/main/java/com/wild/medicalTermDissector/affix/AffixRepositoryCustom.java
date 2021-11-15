package com.wild.medicalTermDissector.affix;


import java.util.List;

public interface AffixRepositoryCustom {

  List<Affix> findByAffixFullText(String letters);

  List<Affix> findByMedTerm(String medTerm);

  List<Affix> findByExactAffix(String letters);

}
