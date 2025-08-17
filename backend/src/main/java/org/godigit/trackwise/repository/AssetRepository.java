package org.godigit.trackwise.repository;

import org.godigit.trackwise.model.Asset;
import org.godigit.trackwise.model.AssetStatus;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface AssetRepository extends JpaRepository<Asset, UUID> {

  List<Asset> findByStatus(AssetStatus status);

  List<Asset> findByCategoryName(String categoryName);

  @Query("SELECT a FROM Asset a WHERE a.purchaseDate < CURRENT_DATE - 1 YEAR")
  List<Asset> findAssetsOlderThanOneYear();


  // Correct Query (in AssetRepository.java)
  @Query("SELECT a FROM Asset a WHERE a.warrantyExpiryDate BETWEEN :from AND :to")
  List<Asset> findByWarrantyExpiryDateBetween(@Param("from") LocalDate from, @Param("to") LocalDate to);
}
