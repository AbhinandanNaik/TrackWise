package org.godigit.trackwise.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;
import org.godigit.trackwise.model.Enum.CheckInOutAction;


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


  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private CheckInOutAction action;

  @Column(nullable = false)
  private Instant checkOutTime;

  private Instant checkInTime;
}
