package org.godigit.trackwise.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

import java.time.LocalDate;

@Getter
@Setter
@Entity
@Table(name = "assets")
public class Asset extends BaseEntity {

  private String name;

  @ManyToOne
  @JoinColumn(name = "category_id")
  private AssetCategory category;

  @Enumerated(EnumType.STRING)
  private AssetStatus status;

  private LocalDate warrantyExpiryDate;

  @ManyToOne
  @JoinColumn(name = "employee_id")
  private Employee assignedTo;

  private LocalDate purchaseDate;

  private String serialNumber;

  @OneToOne(mappedBy = "asset", cascade = CascadeType.ALL, orphanRemoval = true)
  private Warranty warranty;


}
