package org.godigit.trackwise.service;

import org.godigit.trackwise.model.Asset;
import org.godigit.trackwise.model.AssetStatus;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

public interface AssetService {
  Asset create(Asset asset);
  Asset getById(UUID id);
  Page<Asset> list(Pageable pageable);
  Asset update(UUID id, Asset updated);
  void delete(UUID id);

  Asset assignToEmployee(UUID assetId, UUID employeeId);
  Asset unassign(UUID assetId);

  List<Asset> findByStatus(AssetStatus status);
  List<Asset> findWithWarrantyExpiringBetween(LocalDate from, LocalDate to);
}
