package org.godigit.trackwise.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.godigit.trackwise.model.BaseEntity;

@Getter
@Setter
@Entity
@Table(name = "employees")
public class Employee extends BaseEntity {

  private String firstName;
  private String lastName;
  private String email;
  private String phone;

  @ManyToOne
  @JoinColumn(name = "department_id")
  private Department department;
}
