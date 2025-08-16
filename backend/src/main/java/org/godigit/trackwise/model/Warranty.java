package org.godigit.trackwise.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "warranties")
public class Warranty extends BaseEntity {

  @OneToOne
  @JoinColumn(name = "asset_id", unique = true)
  private Asset asset;

  private LocalDate startDate;
  private LocalDate endDate;

  private String vendor;
}
