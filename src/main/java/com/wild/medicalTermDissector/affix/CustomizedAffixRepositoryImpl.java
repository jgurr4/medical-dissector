package com.wild.medicalTermDissector.affix;

import java.util.List;

public class CustomizedAffixRepositoryImpl implements CustomAffixRepository {

  @Override
  public List<Affix> findByAffixFullText(String letters) {
    //Build your custom query here using hibernate-search and lucene.
    // This one is not needed yet, but eventually full-text searches will be implemented.
    return null;
  }

  @Override
  public List<Affix> findByMedTerm(String medTerm) {
    // recreate this query:
    // select id, affix, meaning, examples, readable_affix, locate(readable_affix, ?1) as is_match from affix_view having is_match > 0
    return null;
  }

  @Override
  public List<Affix> findByExactAffix(String letters) {
    // recreate this query:
    // select * from affix_view where readable_affix regexp concat('^', ?1, '$') or readable_affix regexp concat('^', ?1, ',') or readable_affix regexp concat(' ', ?1, ',') or readable_affix regexp concat(' ', ?1, '$')
    return null;
  }
}
