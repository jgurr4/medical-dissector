package com.wild.medicalTermDissector.affix;

import org.hibernate.Session;
import org.hibernate.SessionFactory;
import org.hibernate.query.Query;
import org.springframework.beans.factory.annotation.Autowired;

import javax.persistence.criteria.*;
import java.util.List;

public class AffixRepositoryCustomImpl implements AffixRepositoryCustom {

  private final SessionFactory sessionFactory;

  @Autowired
  AffixRepositoryCustomImpl(SessionFactory sessionFactory) {
    this.sessionFactory = sessionFactory;
  }

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
/*
    SessionFactory sf = new Sess  .createEntityManager()
    Session session = sf.openSession();
*/
    final Session session = sessionFactory.openSession();
    final CriteriaBuilder cb = session.getCriteriaBuilder();
    final CriteriaQuery<Affix> cr = cb.createQuery(Affix.class);
    final Root<Affix> affix = cr.from(Affix.class);
    // FIXME: Currently this command has one minor problem. It generates locate() backwards, which means it fails to get
    // any results from mysql database.
    final CriteriaQuery<Affix> query = cr.multiselect(
        affix.get("id"),
        affix.get("def_id"),
        affix.get("affix"),
        affix.get("meaning"),
        affix.get("examples"),
        affix.get("readable_affix")
    ).where(cb.gt(cb.locate(affix.get("readable_affix"), medTerm), 0));

    final Query<Affix> affixQuery = session.createQuery(query);
    final List<Affix> resultList = affixQuery.getResultList();
    return resultList;
  }

  @Override
  public List<Affix> findByExactAffix(String letters) {
    // recreate this query:
    // select * from affix_view where readable_affix regexp concat('^', ?1, '$') or readable_affix regexp concat('^', ?1, ',') or readable_affix regexp concat(' ', ?1, ',') or readable_affix regexp concat(' ', ?1, '$')
    return null;
  }
}
