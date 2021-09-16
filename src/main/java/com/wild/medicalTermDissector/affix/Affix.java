package com.wild.medicalTermDissector.affix;

import javax.persistence.*;

@Entity
@Table
public class Affix {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Integer id;
  private String affix;
  private String meaning;
  private String examples;
  private String readableAffix;

  public Affix() {
  }

  public Affix(Integer id, String affix, String meaning, String examples, String readableAffix) {
    this.id = id;
    this.affix = affix;
    this.meaning = meaning;
    this.examples = examples;
    this.readableAffix = readableAffix;
  }

  public String getReadableAffix() {
    return readableAffix;
  }

  public void setReadableAffix(String readableAffix) {
    this.readableAffix = readableAffix;
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
      "affix='" + affix + '\'' +
      ", meaning='" + meaning + '\'' +
      ", examples='" + examples + '\'' +
      ", readableAffix='" + readableAffix + '\'' +
      '}';
  }

}
