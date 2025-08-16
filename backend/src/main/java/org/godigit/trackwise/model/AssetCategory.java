package org.godigit.trackwise.model;

import jakarta.persistence.*;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "asset_categories")
public class AssetCategory extends BaseEntity {
  private String name;
  private String description;
}
