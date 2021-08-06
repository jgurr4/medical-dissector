package com.wild.springpractice.student;

import javax.persistence.*;
import java.time.LocalDate;

@Entity
@Table
public class Student {

  @Id
  @GeneratedValue(strategy = GenerationType.AUTO)
  private Long id;
  private String name;
  private String email;
  private LocalDate dob;
  @Column(name="age", columnDefinition = "tinyint generated always as (year(now()) - year(dob))")
  private Integer age;

  public Student() {
  }

  public Student(String name, String email, LocalDate dob) {
    this.name = name;
    this.email = email;
    this.dob = dob;
  }

  public Student(Long id, String name, String email, LocalDate dob) {
    this.id = id;
    this.name = name;
    this.email = email;
    this.dob = dob;
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

  public int getAge() {
    return age;
  }

  public void setAge(int age) {
    this.age = age;
  }

  public String getEmail() {
    return email;
  }

  public void setEmail(String email) {
    this.email = email;
  }

  public LocalDate getDob() {
    return dob;
  }

  public void setDob(LocalDate dob) {
    this.dob = dob;
  }

  @Override
  public String toString() {
    return "Student {" +
      "id=" + id +
      ", name='" + name + '\'' +
      ", age=" + age +
      ", email='" + email + '\'' +
      ", dob=" + dob +
      '}';
  }

}
