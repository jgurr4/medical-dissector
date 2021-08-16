package com.wild.medicalTermDissector.medicalTerms;

import javax.persistence.*;

@Entity
@Table
public class Affix {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private int id;
  private String affix;
  private String meaning;
  private String examples;

  public Affix() {
  }

  public Affix(int id, String affix, String meaning, String examples) {
    this.id = id;
    this.affix = affix;
    this.meaning = meaning;
    this.examples = examples;
  }

  public int getId() {
    return id;
  }

  public void setId(int id) {
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
      "affix='" + affix + '\'' +
      ", meaning='" + meaning + '\'' +
      ", examples='" + examples + '\'' +
      '}';
  }

}
