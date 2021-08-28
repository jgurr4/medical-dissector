package com.wild.medicalTermDissector.medicalTerms;


import javax.persistence.*;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;

@Entity
@Table
public class MedTerm {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;
  private String name;
  @Temporal(TemporalType.TIMESTAMP)
  @Column(name="creationDate") //, columnDefinition = "datetime default current_timestamp")
  private Date creationDate;
  @Temporal(TemporalType.TIMESTAMP)
  @Column(name="lastUpdate") //, columnDefinition = "datetime default current_timestamp on update current_timestamp")
  private Date lastUpdate;
  private String definition;

  public Date getCreationDate() {
    return creationDate;
  }

  public void setCreationDate(Date creationDate) {
    this.creationDate = creationDate;
  }

  public Date getLastUpdate() {
    return lastUpdate;
  }

  public void setLastUpdate(Date lastUpdate) {
    this.lastUpdate = lastUpdate;
  }

  public MedTerm() {
    this.creationDate = new Date();
    this.lastUpdate = new Date();
  }

  public MedTerm(String name, String definition) {
    this.name = name;
    this.definition = definition;
    this.creationDate = new Date();
    this.lastUpdate = new Date();
  }

  public MedTerm(Long id, String name, String definition) {
    this.id = id;
    this.name = name;
    this.definition = definition;
    this.creationDate = new Date();
    this.lastUpdate = new Date();
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
