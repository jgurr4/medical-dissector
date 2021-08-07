package com.wild.medicalTermDissector.medicalTerms;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table
public class MedTerm {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;
  private String name;
  @Column(name="creationDate", columnDefinition = "datetime default current_timestamp")
  private LocalDate creationDate;
  @Column(name="lastUpdate", columnDefinition = "datetime default current_timestamp on update current_timestamp")
  private LocalDate lastUpdate;
  private String definition;

  public MedTerm() {
  }

  public MedTerm(String name, String definition) {
    this.name = name;
    this.definition = definition;
  }

  public MedTerm(Long id, String name, String definition) {
    this.id = id;
    this.name = name;
    this.definition = definition;
  }

  public Long getId() {
    return id;
  }

  public void setId(Long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public String getDefinition() {
    return definition;
  }

  public void setDefinition(String definition) {
    this.definition = definition;
  }

  @Override
  public String toString() {
    return "MedTerm{" +
      "id=" + id +
      ", name='" + name + '\'' +
      ", creationDate=" + creationDate +
      ", lastUpdate=" + lastUpdate +
      ", definition='" + definition + '\'' +
      '}';
  }

}
