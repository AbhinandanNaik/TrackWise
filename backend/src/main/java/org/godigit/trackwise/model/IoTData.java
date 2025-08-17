package org.godigit.trackwise.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;


import java.time.Instant;

@Getter
@Setter
@Entity
@Table(name = "iot_data")
public class IoTData extends BaseEntity {

  @ManyToOne
  @JoinColumn(name = "asset_id")
  private Asset asset;

  private Double temperature;
  private Double batteryLevel;
  private Boolean inUse;

  @Column(nullable = false)
  private Instant timestamp;
  private Double latitude;  // Add this
  private Double longitude;
}
