package com.wild.medicalTermDissector.medicalTerms;

import javax.persistence.*;

@Entity
@Table
public class Affix {

  @Id
  private String affix;
  private String meaning;
  private String examples;

  public Affix() {
  }

  public Affix(String affix, String meaning, String examples) {
    this.affix = affix;
    this.meaning = meaning;
    this.examples = examples;
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
      "affix='" + affix + '\'' +
      ", meaning='" + meaning + '\'' +
      ", examples='" + examples + '\'' +
      '}';
  }

}
