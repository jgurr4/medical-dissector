package com.wild.medicalTermDissector.affix;

import javax.persistence.*;

@Entity
@Table
public class Affix {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Integer id;
  private Integer def_id;
  private String affix;
  private String meaning;
  private String examples;
  private String readable_affix;

  public Affix() {
  }

  public Affix(Integer id, Integer def_id, String affix, String meaning, String examples, String readableAffix) {
    this.id = id;
    this.def_id = def_id;
    this.affix = affix;
    this.meaning = meaning;
    this.examples = examples;
    this.readable_affix = readableAffix;
  }

  public String getReadable_affix() {
    return readable_affix;
  }

  public void setReadable_affix(String readable_affix) {
    this.readable_affix = readable_affix;
  }

  public Integer getId() {
    return id;
  }

  public void setId(Integer id) {
    this.id = id;
  }

  public String getAffix() {
    return affix;
  }

  public void setAffix(String affix) {
    this.affix = affix;
  }

  public String getMeaning() {
    return meaning;
  }

  public void setMeaning(String meaning) {
    this.meaning = meaning;
  }

  public String getExamples() {
    return examples;
  }

  public void setExamples(String examples) {
    this.examples = examples;
  }

  @Override
  public String toString() {
    return "Affix{" +
        "id=" + id +
        ", def_id=" + def_id +
        ", affix='" + affix + '\'' +
        ", meaning='" + meaning + '\'' +
        ", examples='" + examples + '\'' +
        ", readableAffix='" + readable_affix + '\'' +
        '}';
  }

  public Integer getDef_id() {
    return def_id;
  }

  public void setDef_id(Integer def_id) {
    this.def_id = def_id;
  }
}
