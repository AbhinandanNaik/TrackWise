package org.godigit.trackwise.repository;

import org.godigit.trackwise.model.Asset;
import org.godigit.trackwise.model.AssetStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface AssetRepository extends JpaRepository<Asset, UUID> {

  List<Asset> findByStatus(AssetStatus status);

  List<Asset> findByCategory(String category);

  @Query("SELECT a FROM Asset a WHERE a.purchaseDate < CURRENT_DATE - 365")
  List<Asset> findAssetsOlderThanOneYear();

  @Query("SELECT a FROM Asset a WHERE a.warrantyExpiryDate BETWEEN CURRENT_DATE AND CURRENT_DATE + 30")
  List<Asset> findAssetsWithExpiringWarranty();

  @Query("SELECT * FROM assets WHERE warranty_expiry_date BETWEEN :from AND :to;")
  List<Asset> findByWarrantyExpiryDateBetween(LocalDate from, LocalDate to);
}
