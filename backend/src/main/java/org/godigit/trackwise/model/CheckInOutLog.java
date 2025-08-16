package org.godigit.trackwise.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "check_in_out_logs")
public class CheckInOutLog extends BaseEntity {

  @ManyToOne
  @JoinColumn(name = "asset_id")
  private Asset asset;

  @ManyToOne
  @JoinColumn(name = "employee_id")
  private Employee employee;

  @Column(nullable = false)
  private Instant checkOutTime;

  private Instant checkInTime;
}
