package com.wild.springpractice.student;

import javax.persistence.*;
import java.time.LocalDate;
import java.time.Period;

@Entity  // This is for hibernate to create table.
@Table
public class Student {

  @Id
  @SequenceGenerator(
    name = "student_sequence",
    sequenceName = "student_sequence",
    allocationSize = 1)
  @GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "student_sequence")
  private Long id;   // The code above is how you make spring auto-generate the id.

  private String name;
  private String email;
  private LocalDate dob;
  // This tells spring boot that we don't need to construct a age column because we will calculate it based on dob instead.
  @Transient
  private Integer age;

  public Student() {
  }

  public Student(String name, String email, LocalDate dob) {
    this.name = name;
    this.email = email;
    this.dob = dob;
  }

  public Student(long id, String name, String email, LocalDate dob) {
    this.id = id;
    this.name = name;
    this.email = email;
    this.dob = dob;
  }

  public long getId() {
    return id;
  }

  public void setId(long id) {
    this.id = id;
  }

  public String getName() {
    return name;
  }

  public void setName(String name) {
    this.name = name;
  }

  public int getAge() {
    return Period.between(this.dob, LocalDate.now()).getYears();
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
